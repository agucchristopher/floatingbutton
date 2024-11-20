// FloatingWidget.js
import { NativeModules, Platform, PermissionsAndroid } from "react-native";

const { FloatingWidget } = NativeModules;

export default class FloatingWidgetService {
  static async checkPermission() {
    if (Platform.OS !== "android") {
      return false;
    }
    return await FloatingWidget.checkOverlayPermission();
  }

  static async requestPermission() {
    if (Platform.OS !== "android") {
      return false;
    }
    return await FloatingWidget.requestOverlayPermission();
  }

  static async showWidget() {
    if (Platform.OS !== "android") {
      return false;
    }
    return await FloatingWidget.showWidget();
  }

  static async hideWidget() {
    if (Platform.OS !== "android") {
      return false;
    }
    return await FloatingWidget.hideWidget();
  }
}

// App.js usage example
