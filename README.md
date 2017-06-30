# react-native-sunmi-inner-scanner
http://docs.sunmi.com/htmls/index.html?lang=zh##V1文档资源  根据商米V1文档开发打印接口
(React native plugin Referring the sunmi V1 scanner document and demos)

_ **Caution: this is not the official project. I share it because I am working on this device but no any official support in react-native It's welcome to ask any question about the usage,problems or feature required, I will support ASAP.**_

======================================================================================
**Installation:**

Step 1. install with npm:

```bash
npm install januslo/react-natvie-sunmi-inner-scanner --save
```

or you may need to install via the clone address directly:

```bash 
npm install https://github.com/januslo/react-native-sunmi-inner-scanner.git --save
```

Step 2:

Links this plugin to your project.

```bash
react-native link react-natvie-sunmi-inner-scanner
```

or you may need to link manually 
* modify settings.gradle

```javascript 
include ':react-native-sunmi-inner-scanner'
project(':react-native-sunmi-inner-scanner').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-sunmi-inner-scanner/android')
```

* modify  app/build.gradle,add dependenceie：

```javascript
compile project(':react-native-sunmi-inner-scanner')
```

* adds package references to  MainPackage.java 

```java

import com.sunmi.scanner.SunmiInnerScannerPackage;
...

 @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
            new SunmiInnerScannerPackage()
      );
    }

```

Step 3: refer in the javascript:
*Using the default inner scanner:
```javascript
import SunmiInnerScanner from 'react-native-sunmi-inner-scanner';

```
*Customer your preview with the SunmiScannerView

```javascript
import {SunmiScannerView} from 'react-native-sunmi-inner-scanner';

```

**Usage:**
You may need to refer the excamples from the 'examples' folder of the source code.

*Using SunmiInnerScanner

you can open the scanner with default options:
```javascript

 async _openDefaultScanner(){
        let result = await SunmiInnerScanner.openScanner();
        this.setState({result: JSON.stringify(result)}
        ,()=>{
            console.log(this.state.result);
        });

    }
    
```
or you can specify the options:
```javascript
 async _openScannerWithOptions(){
        let options={
            showSetting:true,
            showAlbum:true,
            paySound:true,
            payVibrate:true,// V1 not support
        }
        let result = await SunmiInnerScanner.openScannerWithOptions(options);
        this.setState({result: JSON.stringify(result)}
        ,()=>{
            console.log(this.state.result);
        });

    }

```

*Using SunmiScannerView

```javascript

 render() {
        return <View style={styles.container}>
            <Text>Scan Result: {this.state.result}</Text>
            <View>
                <SunmiScannerView style={styles.scanner} onCodeScan={(data)=>{
                this.setState({
                result:JSON.stringify(data)
                })
            }
            }>
                </SunmiScannerView>
                <View style={styles.finder}></View>
            </View>
        </View>
    }


```

providing the properties definition for referring:
```javascript


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
            PropTypes.number
        ]),
        onCodeScan: React.PropTypes.func
    };
    static defaultProps = {
        xDensity: 2,
        yDensity: 2,
        mutilScanEnable: 0,
        inverseEnable: 1,
        onCodeScan: function (result) {
            console.log(result);
        },
        scanInterval:1000, // interval of scan operation after last record was recongized. 
        mute:0 // mute the "bee" sound on success. 1 - mute;0 - not mute
    }
```