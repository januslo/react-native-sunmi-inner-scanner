import React,{Component,PropTypes} from 'react';
import { NativeModules,
    requireNativeComponent,
    View,
    DeviceEventEmitter
} from 'react-native';

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
        xDensity: PropTypes.oneOfType([
            PropTypes.number
        ]),
        yDensity: PropTypes.oneOfType([
            PropTypes.number
        ]),
        mutilScanEnable: PropTypes.oneOfType([
            PropTypes.number
        ]),
        inverseEnable: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),
        onCodeScan: React.PropTypes.func,
        scanInterval:PropTypes.oneOfType([
            PropTypes.number
        ]),
        mute:PropTypes.oneOfType([
            PropTypes.number
        ])
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
