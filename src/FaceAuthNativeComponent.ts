import {
  codegenNativeComponent,
  type ViewProps,
  type ViewStyle,
} from 'react-native';

interface FaceAuthProps extends ViewProps {
  style?: ViewStyle;
  onSuccess?: (data: any) => void;
  onFailure?: (err: string) => void;
}

export default codegenNativeComponent<FaceAuthProps>('FaceAuth');
