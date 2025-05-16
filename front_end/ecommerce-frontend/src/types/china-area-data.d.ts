declare module 'china-area-data' {
  interface AreaData {
    [key: string]: {
      [key: string]: string;
    };
  }

  const areaData: AreaData;
  export default areaData;
} 