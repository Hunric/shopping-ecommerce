/**
 * 地区数据接口
 */
export interface Region {
  code: string;
  name: string;
  children?: Region[];
}

/**
 * 地址服务接口
 */
export interface AddressService {
  /**
   * 获取所有省份数据
   */
  getProvinces(): Promise<Region[]>;
  
  /**
   * 获取指定省份的城市数据
   * @param provinceCode 省份代码
   */
  getCities(provinceCode: string): Promise<Region[]>;
  
  /**
   * 获取指定城市的区县数据
   * @param cityCode 城市代码
   */
  getDistricts(cityCode: string): Promise<Region[]>;
  
  /**
   * 根据代码获取完整的地址名称(省市区)
   * @param codes 省市区代码数组
   */
  getFullAddressText(codes: string[]): Promise<string>;
} 