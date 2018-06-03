
import React, { Component } from 'react';
import {
  requireNativeComponent,
  Modal,
  View,
  Image,
  StyleSheet,
  TouchableWithoutFeedback,
  ViewPropTypes,
} from 'react-native';
import PropTypes from 'prop-types';
import FetchUtil from './Lib/FetchUtil';

const RCT_VIDEO_REF = 'video';
const LOADING_IMG = require('./Assets/loading.gif');

// Fallback when RN version is < 0.44
const viewPropTypes = ViewPropTypes || View.propTypes;

const STS_URL = 'your sts url';

/**
 * example:
 *  1. <VideoView fullscreen source={{ vid: n.videoId }} poster={n.videoUrl} />
 *  2. <VideoView fullscreen source={{ url: localUrl }} />
 *  3. <VideoView ref={(r) => { this.video = r; }} hide fullscreen />
 *     this.video.start(vid);
 */
class Video extends Component {
  static defaultProps = {
    poster: '',
    contextStyle: {},
    animationType: 'slide',
    hide: false,
    source: {        // url/vid
      url: '',       // local url
    },
    looping: true,
  };

  constructor(props) {
    super(props);
    this._root = null;
    this._prepared = false;
    this.state = {
      vid: '',
      akid: '',
      aks: '',
      token: '',
      localUrl: '',
      playing: false,
      loading: false,
    };
  }

  async start(vid) { // eslint-disable-line
    console.log('start video');
    const { source } = this.props;
    const { akid, aks, token } = await this._fetchToken();
    const state = { playing: true, akid, aks, token };
    if (vid) {
      state.vid = vid;
    } else if (source && source.url) {
      state.localUrl = source.url;
    } else if (source && source.vid) {
      state.vid = source.vid;
    }
    if (!this._prepared) {
      state.loading = true;
    }
    this.setState(state);
  }

  pause() {
    this.setState({ playing: false, loading: false });
  }

  load() {
    // TODO: 新增native 事件，实现加载监听
    setTimeout(() => {
      this.start();
      setTimeout(() => {
        this.pause();
      }, 300);
    }, 10);
  }

  setNativeProps(nativeProps) {
    this._root.setNativeProps(nativeProps);
  }

  async _fetchToken() {
    const url = `${STS_URL}shortvideo/get`;
    const res = await FetchUtil.js.get(url);
    return {
      akid: res.AccessKeyId,
      aks: res.AccessKeySecret,
      token: res.SecurityToken,
    };
  }

  _toggle() {
    console.log('toggle video');
    if (this.state.playing) {
      this.pause();
    } else {
      this.start();
    }
  }

  _onVideoPrepared = () => {
    this._prepared = true;
  }

  _onVideoEnd = () => {
    if (!this.props.looping) {
      this.pause();
    }
  }

  _onVideoError = ({ nativeEvent }) => {
    console.log(nativeEvent.code, nativeEvent.msg);
    alert(nativeEvent.msg);
    this.pause();
    // this.setState({ loading: false, });
  }

  _onVideoLoadStart = () => {
    this.setState({ loading: true });
  }

  _onVideoLoadEnd = () => {
    this.setState({ loading: false });
  }

  _onRequestClose() {
    this.pause();

    if (this.props.fullscreen) { // modal 关闭会销毁video
      this._prepared = false;
    }
  }

  _renderFullScreen() {
    const { contextStyle, looping, fullscreen, hide, poster } = this.props;
    const { loading, playing, vid, akid, aks, token, localUrl } = this.state;
    const fullScreenStyle = fullscreen && playing ? Styles.fullScreen : {};
    const nativeProps = Object.assign({}, {
      style: [Styles.contextStyle, contextStyle, fullScreenStyle],
      playing,
      looping,
      onVideoPrepared: this._onVideoPrepared,
      onVideoEnd: this._onVideoEnd,
      onVideoError: this._onVideoError,
      onVideoLoadStart: this._onVideoLoadStart,
      onVideoLoadEnd: this._onVideoLoadEnd,
    });

    if (localUrl) {
      nativeProps.localSrc = localUrl;
    } else if (vid) {
      nativeProps.src = { vid, akid, aks, token };
    }
    const hideStyle = hide ? Styles.hide : {};
    // console.info('video full native props', nativeProps);
    return (
      <TouchableWithoutFeedback onPress={() => this._toggle()}>
        <View style={[Styles.base, Styles.contextStyle, this.props.contextStyle, hideStyle]}>
          {poster !== '' && !playing ? (<Image style={Styles.poster} source={{ uri: poster }} />) : null}
          {/* {!playing ? (<Icon name={'play-circle-o'} style={Styles.icon} />) : null} */}
          {!playing ? (<Text>播放</Text>) : null}
          <Modal
            visible={this.state.playing}
            animationType={this.props.animationType}
            onRequestClose={() => { this._onRequestClose(); }}
          >
            <View style={Styles.modalContent}>
              <RCTVideoView
                ref={RCT_VIDEO_REF}
                {...nativeProps}
                playing={this.state.playing}
                onStartShouldSetResponder={() => true}
                onResponderGrant={() => this._onRequestClose()}
              />
              {
                loading ? (<Image style={Styles.loading} source={LOADING_IMG} />) : null
              }
            </View>
          </Modal>
        </View>
      </TouchableWithoutFeedback>
    );
  }

  _renderVideo() {
    const { contextStyle, looping, hide, poster } = this.props;
    const { loading, playing, vid, akid, aks, token, localUrl } = this.state;
    const nativeProps = Object.assign({}, {
      style: [Styles.contextStyle, contextStyle],
      playing,
      looping,
      onVideoPrepared: this._onVideoPrepared,
      onVideoEnd: this._onVideoEnd,
      onVideoError: this._onVideoError,
      onVideoLoadStart: this._onVideoLoadStart,
      onVideoLoadEnd: this._onVideoLoadEnd,
    });
    if (localUrl) {
      nativeProps.localSrc = localUrl;
    } else if (vid) {
      nativeProps.src = { vid, akid, aks, token };
    }
    const hideStyle = hide ? Styles.hide : {};
    // console.info('video native props', nativeProps);
    return (
      <TouchableWithoutFeedback style={{ flex: 1 }} onPress={() => this._toggle()}>
        <View style={[Styles.base, hideStyle]}>
          <RCTVideoView
            ref={RCT_VIDEO_REF}
            {...nativeProps}
            playing={this.state.playing}
          />
          {
            poster !== '' && !playing ? (<Image style={Styles.poster} source={{ uri: poster }} />) : null
          }
          {/* {
            !playing ? (<Icon name={'play-circle-o'} style={Styles.icon} />) : null
          } */}
          {
            !playing ? (<Text>播放</Text>) : null
          }
          {
            loading ? (<Image style={Styles.loading} source={LOADING_IMG} />) : null
          }
        </View>
      </TouchableWithoutFeedback>
    );
  }

  render() {
    const { fullscreen } = this.props;
    if (fullscreen) {
      return this._renderFullScreen();
    }
    return this._renderVideo();
  }
}

const Styles = StyleSheet.create({
  base: {
    // flexDirection: 'row',
    width: '100%',
    justifyContent: 'center',
    alignItems: 'center',
    overflow: 'hidden',
    backgroundColor: '#000'
  },
  modalContent: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    overflow: 'hidden',
    backgroundColor: '#000'
  },
  contextStyle: {
    width: '99%',
    height: 240,
  },
  fullScreen: {
    width: '100%',
    height: 380,
  },
  hide: {
    width: 0,
    height: 0,
  },
  poster: {
    position: 'absolute',
    // left: 0,
    // top: 0,
    // right: 0,
    // bottom: 0,
    width: '100%',
    height: '100%',
    resizeMode: 'cover',
  },
  icon: {
    position: 'absolute',
    fontSize: 30,
    color: '#fff',
    backgroundColor: 'transparent'
  },
  loading: {
    position: 'absolute',
    width: 50,
    height: 50,
  }
});

const VideoPlayerView = {
  name: 'VideoPlayerView',
  propTypes: {
    style: viewPropTypes.style,
    src: PropTypes.shape({
      vid: PropTypes.string,
      akid: PropTypes.string,
      aks: PropTypes.string,
      token: PropTypes.string,
    }),
    localSrc: PropTypes.string,
    playing: PropTypes.bool,
    looping: PropTypes.bool,
    onVideoPrepared: PropTypes.func,
    onVideoEnd: PropTypes.func,
    onVideoError: PropTypes.func,
    onVideoLoadStart: PropTypes.func,
    onVideoLoadEnd: PropTypes.func,
    ...viewPropTypes, // 包含默认的View的属性，如果没有这句会报‘has no propType for native prop’错误
  }
};
const RCTVideoView = requireNativeComponent('RNVideoPlayerView', VideoPlayerView);

export default Video;
