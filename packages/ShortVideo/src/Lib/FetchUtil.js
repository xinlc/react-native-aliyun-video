

const TIMEOUT_MS = 60 * 1000;
const timeoutMessage = 'time out';

const FetchUtil = {
  // post请求
  post(url, data, onSuccess, onError) {
    const fetchOptions = {
      method: 'POST',
      headers: {
        Accept: 'application/json,text/plain',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    };

    return this.request(url, fetchOptions, onSuccess, onError);
  },

  // get请求
  get(url, onSuccess, onError) {
    const fetchOptions = {
      method: 'GET',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      }
    };
    return this.request(url, fetchOptions, onSuccess, onError);
  },

  // put请求
  put(url, data, onSuccess, onError) {
    const fetchOptions = {
      method: 'PUT',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    };

    return this.request(url, fetchOptions, onSuccess, onError);
  },

  delete(url, onSuccess, onError) {
    const fetchOptions = {
      method: 'DELETE',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      }
    };

    if (arguments[1] && typeof arguments[1] != 'function') {
      const data = arguments[1];
      onSuccess = arguments[2];
      onError = arguments[3];
      fetchOptions.body = JSON.stringify(data);
    }

    return this.request(url, fetchOptions, onSuccess, onError);
  },

  request(url, fetchOptions, onSuccess = Function, onError) {
    console.info('FetchUtil.request.fetchOptions', url, fetchOptions);
    let status = null;
    let defaultOnError = null;
    if (!onError) {
      defaultOnError = function () {
      };
      onError = defaultOnError;
    }
    return new Promise(function (resolve, reject) { // eslint-disable-line
      this.timeout(TIMEOUT_MS, fetch(url, fetchOptions))
      .then((response) => {
        status = response.status;
        return response.text();
      })
      .then((responseText) => {
        // console.info('Components.FetchUtil.responseText', responseText);
        let data;
        try {
          data = JSON.parse(responseText);
        } catch (e) {
          console.info('Components.FetchUtil.request', responseText);
          // status = 202;
          data = {
            code: status,
            msg: JSON.stringify(e)
          };
        }
        if ([200, 201, 301, 304].includes(status)) {
          resolve(data);
          onSuccess(data);
        } else {
          console.info('Components.FetchUtil.request.error', data);
          reject(data);
          onError(data);
        }
      }).catch((error) => {
        if (error.message == 'timeout' || error.message == 'Network request failed') {
          const clientData = {
            code: 408,
            msg: timeoutMessage
          };
          reject(error);
          if (onError === defaultOnError) {
            alert(clientData.msg);
          } else {
            onError(clientData);
          }
        }
      }
    );
    }.bind(this));
  },

  timeout(ms, promise) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        reject(new Error('timeout'));
      }, ms);
      promise.then(resolve, reject);
    });
  },
};

export default FetchUtil;
