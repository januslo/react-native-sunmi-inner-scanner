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
    Dimensions,
    Navigator
} from 'react-native';

import SunmiInnerScanner, {SunmiScannerView} from 'react-native-sunmi-inner-scanner';
import scannerPrevew from './scannerPrevew';

var {height, width} = Dimensions.get('window');
export default class example extends Component {
    constructor(props){
        super(props);
        this.state={
            result:""
        }
    }

    async _openDefaultScanner(){
        let result = await SunmiInnerScanner.openScanner();
        this.setState({result: JSON.stringify(result)});

    }
    render() {
        return (
            <View style={styles.container}>
                <View><Text>Scan Result:{this.state.result}</Text></View>
                <View style={{flex: 1,justifyContent: 'center',alignItems:'center'}}>
                    <TouchableOpacity style={styles.buttonstyle} onPress={() =>this._openDefaultScanner() }>
                        <Text style={{fontSize:16}}>Default Scanner</Text>
                    </TouchableOpacity>
                    <TouchableOpacity style={styles.buttonstyle} onPress={() =>{this.props.navigator.push({
                    component:scannerPrevew
                    })}}>
                        <Text style={{fontSize:16}}>Customer Scanner Preview</Text>
                    </TouchableOpacity>
                </View>
            </View>
        );
    }
}


const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor:"#fff"
    },
    textview: {
        height: 45,
        backgroundColor:'#696969',
        justifyContent: 'center',
        alignItems:'center'
    },
    textstyle: {
        fontSize: 18,
        color: '#fff',
    },
    buttonstyle:{
        width:width-64,
        height:100,
        marginLeft:32,
        marginRight:32,
        justifyContent: 'center',
        alignItems:'center',
        backgroundColor:"#8bc8ef",
        borderRadius:10,
        marginBottom:50
    },
    userSetting:{
        position:'absolute',
        top:12,
        right:10,
        height:20,
        width:20
    },
    userSettingImg:{
        height:20,
        width:20
    },
    scanner:{
        height:400,
        width:400,
        borderStyle:'solid',
        borderWidth:1,
        borderColor:'#ff0000'
    }
});
