/**
 * Created by januslo on 2017/5/17.
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
    Navigator,
    Button
} from 'react-native';

import {SunmiScannerView} from 'react-native-sunmi-inner-scanner';
var {height, width} = Dimensions.get('window');
export default class scannerPrevew extends Component {
    constructor(props) {
        super(props);
        this.state = {
            result: "",
            mute:0,
            interval:500
        }
    }

    render() {
        return <View style={styles.container}>
            <Text>Scan Result: {this.state.result}</Text>
            <Button title={this.state.mute?"not mute":"mute"} onPress={()=>{
                    this.setState({
                    mute:this.state.mute?0:1
                    })
            }
            }></Button>
            <View>
                <SunmiScannerView style={styles.scanner} mute={this.state.mute} scanInterval={this.state.interval}  onCodeScan={(data)=>{
                this.setState({
                result:JSON.stringify(data)+" [mute:"+this.state.mute+"]"
                })
            }
            }>
                </SunmiScannerView>
                <View style={styles.finder}></View>
            </View>
        </View>
    }

}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#fff",
    },
    scanner: {
        height: 450,
        justifyContent: 'center',
        alignItems: 'center'
    },
    finder: {
        position: 'absolute',
        top: 110,
        left: 40,
        width: 280,
        height: 250,
        borderStyle: 'solid',
        borderColor: '#00aa00',
        borderWidth: 1,
        backgroundColor: 'rgba(52, 52, 52, 0)'
    }
});