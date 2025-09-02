## React Native:

- [New Architecture](https://github.com/reactwg/react-native-new-architecture/discussions/27)

## Android

- [Camera-X intergration](https://medium.com/@rohitgarg2016/to-integrate-a-simple-camera-using-camerax-in-a-kotlin-android-app-cefa4cfd0edc)

## Usefull Avd Commands:

- `emulator -webcam-list` : List host camera devices.
- `emulator -avd Pixel_7_API_35 -gpu host` : Run avd on dGpu ie (Nvidia Graphics Card).Set an alias for emulator in bashrc so that emulator can use nvidia like below.

``` ~ ~/.bashrc
## make emulator use Nvidia Gpu
alias emulator='__NV_PRIME_RENDER_OFFLOAD=1 __GLX_VENDOR_LIBRARY_NAME=nvidia emulator'
```