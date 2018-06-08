import React,{Component} from 'react';
import { NativeModules,
    requireNativeComponent,
    View,
    DeviceEventEmitter
} from 'react-native';
import PropTypes from 'prop-types'

const SunmiInnerScanner = NativeModules.SunmiInnerScanner;
export default SunmiInnerScanner;
export class SunmiScannerView extends Component {

    componentWillMount() {
        if (this.props.onCodeScan){
            this.cameraBarCodeReadListener = DeviceEventEmitter.addListener('SunmiInnerScannerView.RESULT',
                (data)=> {
                this.props.onCodeScan(data.result || []);
            });
        }
    }

    componentWillUnmount() {
        if (this.cameraBarCodeReadListener) {
            this.cameraBarCodeReadListener.remove();
        }
    }

    static propTypes = {
        ...View.propTypes,
        xDensity: PropTypes.number,
        yDensity: PropTypes.number,
        mutilScanEnable: PropTypes.number,
        inverseEnable: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),
        onCodeScan: PropTypes.func,
        scanInterval: PropTypes.number,
        mute: PropTypes.number
    };
    static defaultProps = {
        xDensity: 2,
        yDensity: 2,
        mutilScanEnable: 0,
        inverseEnable: 1,
        onCodeScan: function (result) {
            console.log(result);
        },
        scanInterval:1000,
        mute:0
    }

    render() {
        return <SunmiScanner {...this.props} />;
    }
}
const SunmiScanner = requireNativeComponent('SunmiScanner', SunmiScannerView);
