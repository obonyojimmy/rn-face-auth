import { codegenNativeComponent, type ViewProps } from 'react-native';

import type {
  DirectEventHandler,
  // @ts-ignore TODO: remove once there is a .d.ts file with definitions
} from 'react-native/Libraries/Types/CodegenTypes';

type FaceAuthSuccessEvent = {
  faceData: string; // can be JSON stringified data
};

type FaceAuthFailureEvent = {
  error: string;
};

export interface FaceAuthProps extends ViewProps {
  color?: string;
  onSuccess?: DirectEventHandler<FaceAuthSuccessEvent>;
  onFailure?: DirectEventHandler<FaceAuthFailureEvent>;
}

export default codegenNativeComponent<FaceAuthProps>('RnFaceAuthView');
