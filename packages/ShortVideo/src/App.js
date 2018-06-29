/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */

import React, { Component } from 'react';
import {
  StyleSheet,
  View,
  Button,
} from 'react-native';
import VideoView from './VideoView';
import RNShortVideo from './Native/RNShortVideo'; // eslint-disable-line

type Props = {};
export default class App extends Component<Props> {
  state = {
    vedioId: null,
    vedioUrl: null,
    path: null,
  };

  // 录制短视频
  onRecord = () => {
    RNShortVideo.recordShortVideo()
      .then((path) => {
        this.setState({ path });
      })
      .catch((err) => {
        console.error(err);
      });
  };

  onUpload = () => {
    // 通过STS 获取token
    const uptoken = null; // TODO: fetch token
    if (!uptoken) {
      alert('请先通过STS获取TOKEN');
      return;
    }

    // upload video
    RNShortVideo.uploadVideo({
      mp4Path: this.state.path,
      accessKeyId: uptoken.AccessKeyId,
      accessKeySecret: uptoken.AccessKeySecret,
      securityToken: uptoken.SecurityToken,
      expriedTime: uptoken.Expiration,
    }).then((result) => {
      this.setState({
        vedioId: result.vid,
        vedioUrl: result.imageUrl
      });
    }).catch((e) => {
      console.error('error', e);
    });
  };

  render() {
    return (
      <View style={styles.container}>
        <Button
          onPress={this.onRecord}
          title="录制短视频"
          color="#841584"
        />
        <View style={{ height: 20 }} />
        <Button
          onPress={this.onUpload}
          title="上传短视频"
          color="#841584"
        />
        <View style={{ height: 20 }} />
        {
          this.state.videoId ? (
            <VideoView
              fullscreen
              source={{ vid: this.state.videoId }}
              poster={this.state.vedioUrl}
            />
          ) : null
        }
        <View style={{ height: 20 }} />
        {
          this.state.path ? (
            <VideoView
              fullscreen
              source={{ url: this.state.path }}
              poster="http://lorempixel.com/300/300/"
            />
          ) : null
        }
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 10,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
});
