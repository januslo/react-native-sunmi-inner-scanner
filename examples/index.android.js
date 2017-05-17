/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
    AppRegistry,
    View,
    Text,
    StyleSheet,
    TouchableOpacity,
    Image,
    TouchableHighlight,
    BackAndroid,
    Dimensions
} from 'react-native';
import {Navigator} from 'react-native-deprecated-custom-components';
var _navigator;
import example from './example';
export default class examples extends Component {
    configureScence(){
        return Navigator.SceneConfigs.FloatFromRight;
    }

    renderScene(route, navigator){
        _navigator = navigator;
        let Component = route.component;
        return <Component route={route} navigator={navigator} {...route.passProps}/>
    }

    render(){
        var renderScene = this.renderScene;
        var configureScene = this.configureScence;
        return (
            <Navigator
                style={{flex:1}}
                initialRoute={{ name: 'Example', component:example }}
                configureScene={configureScene}
                renderScene={renderScene}
            />
        );
    }
}

AppRegistry.registerComponent('examples', () => examples);
