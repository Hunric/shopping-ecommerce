declare module '@/api/verification' {
  export const verificationApi: {
    /**
     * 发送验证码
     * @param email - 邮箱地址
     * @param purpose - 用途（login/register）
     */
    sendVerificationCode: (email: string, purpose: string) => Promise<any>;
    
    /**
     * 验证验证码
     * @param email - 邮箱地址
     * @param code - 验证码
     * @param purpose - 用途（login/register）
     */
    verifyCode: (email: string, code: string, purpose: string) => Promise<any>;
  };
} 