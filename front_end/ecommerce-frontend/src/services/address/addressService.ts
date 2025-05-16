import { ref } from 'vue';
import type { Ref } from 'vue';
import type { Region, AddressService } from './types';
import areaData from 'china-area-data';

// 缓存已加载的地区数据
let cachedRegions: Ref<Region[]> = ref([]);
let isLoading = ref(false);
let loadPromise: Promise<Region[]> | null = null;

/**
 * 地址服务实现
 */
class AddressServiceImpl implements AddressService {
  /**
   * 加载省市区数据
   * 直接使用china-area-data包中的数据
   */
  private async loadRegionsData(): Promise<Region[]> {
    // 如果已经加载过或正在加载，直接返回
    if (cachedRegions.value.length > 0) {
      return cachedRegions.value;
    }
    
    // 如果有正在进行的加载操作，等待它完成
    if (loadPromise) {
      return loadPromise;
    }
    
    // 开始加载
    isLoading.value = true;
    
    try {
      // 创建一个Promise来处理数据转换
      loadPromise = new Promise<Region[]>((resolve) => {
        const provinces: Region[] = [];
        
        // 处理省级数据 (86是中国的代码，下面是各省份)
        Object.keys(areaData['86']).forEach(provinceCode => {
          const province: Region = {
            code: provinceCode,
            name: areaData['86'][provinceCode],
            children: []
          };
          
          // 处理市级数据
          if (areaData[provinceCode]) {
            Object.keys(areaData[provinceCode]).forEach(cityCode => {
              const city: Region = {
                code: cityCode,
                name: areaData[provinceCode][cityCode],
                children: []
              };
              
              // 处理区/县级数据
              if (areaData[cityCode]) {
                Object.keys(areaData[cityCode]).forEach(districtCode => {
                  const district: Region = {
                    code: districtCode,
                    name: areaData[cityCode][districtCode]
                  };
                  city.children!.push(district);
                });
              }
              
              province.children!.push(city);
            });
          }
          
          provinces.push(province);
        });
        
        cachedRegions.value = provinces;
        resolve(provinces);
      });
      
      return await loadPromise;
    } finally {
      isLoading.value = false;
      loadPromise = null;
    }
  }
  
  /**
   * 获取所有省份
   */
  async getProvinces(): Promise<Region[]> {
    const regions = await this.loadRegionsData();
    return regions;
  }
  
  /**
   * 获取指定省份的城市
   */
  async getCities(provinceCode: string): Promise<Region[]> {
    const regions = await this.loadRegionsData();
    const province = regions.find(p => p.code === provinceCode);
    return province?.children || [];
  }
  
  /**
   * 获取指定城市的区县
   */
  async getDistricts(cityCode: string): Promise<Region[]> {
    const regions = await this.loadRegionsData();
    
    for (const province of regions) {
      if (province.children) {
        const city = province.children.find(c => c.code === cityCode);
        if (city && city.children) {
          return city.children;
        }
      }
    }
    
    return [];
  }
  
  /**
   * 获取完整地址文本
   */
  async getFullAddressText(codes: string[]): Promise<string> {
    if (!codes || codes.length === 0) {
      return '';
    }
    
    const regions = await this.loadRegionsData();
    const addressParts: string[] = [];
    
    // 查找省份
    if (codes.length > 0) {
      const province = regions.find(p => p.code === codes[0]);
      if (province) {
        addressParts.push(province.name);
        
        // 查找城市
        if (codes.length > 1 && province.children) {
          const city = province.children.find(c => c.code === codes[1]);
          if (city) {
            addressParts.push(city.name);
            
            // 查找区县
            if (codes.length > 2 && city.children) {
              const district = city.children.find(d => d.code === codes[2]);
              if (district) {
                addressParts.push(district.name);
              }
            }
          }
        }
      }
    }
    
    return addressParts.join(' ');
  }
}

// 创建服务单例
const addressService = new AddressServiceImpl();

export default addressService; 