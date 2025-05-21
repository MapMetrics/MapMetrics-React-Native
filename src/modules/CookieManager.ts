import { NativeModules } from 'react-native';

interface IMLRNCookieManager {
  setCookie(url: string, value: string): Promise<boolean>;
  getCookie(url: string): Promise<string | null>;
}

const { MLRNCookieManager } = NativeModules;

class CookieManager {
  private static instance: CookieManager;
  private gatewayUrl = 'https://gateway.mapmetrics.org';

  private constructor() {}

  public static getInstance(): CookieManager {
    if (!CookieManager.instance) {
      CookieManager.instance = new CookieManager();
    }
    return CookieManager.instance;
  }

  /**
   * Set a cookie received from gateway.mapmetrics.org
   * @param cookie The cookie value received from the gateway
   */
  public async setGatewayCookie(cookie: string): Promise<void> {
    console.log('[CookieManager] Setting gateway cookie:', cookie);
    try {
      // Pass through the cookie as received from the server
      const result = await MLRNCookieManager.setCookie(this.gatewayUrl, cookie);
      console.log('[CookieManager] Native setCookie result:', result);
    } catch (error) {
      console.error('[CookieManager] Error setting cookie:', error);
      throw error;
    }
  }

  /**
   * Get the current cookie for gateway.mapmetrics.org
   */
  public async getGatewayCookie(): Promise<string | null> {
    console.log('[CookieManager] Getting gateway cookie');
    try {
      const cookie = await MLRNCookieManager.getCookie(this.gatewayUrl);
      console.log('[CookieManager] Retrieved cookie:', cookie);
      return cookie;
    } catch (error) {
      console.error('[CookieManager] Error getting cookie:', error);
      throw error;
    }
  }

  /**
   * Clear the gateway cookie
   */
  public async clearGatewayCookie(): Promise<void> {
    console.log('[CookieManager] Clearing gateway cookie');
    try {
      await MLRNCookieManager.setCookie(this.gatewayUrl, '');
      console.log('[CookieManager] Cookie cleared');
    } catch (error) {
      console.error('[CookieManager] Error clearing cookie:', error);
      throw error;
    }
  }
}

export default CookieManager.getInstance(); 