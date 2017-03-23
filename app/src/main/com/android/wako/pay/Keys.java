/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.android.wako.pay;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	public static final String PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKgjVNXsU+OJgQYcCzeflrWMf9I0Mt6T8tVJhpCsUmRSikFvNn5E0h/Z0Sx44dtppdzuDTF0qAO3B0s88dafA2+VZDsMSQpadtPlRk6E2I61xfmfIuE20Ug4n2mpikulZUSMLbKoSQ/QE1BDo9L9h++kiXx8oAZAI4jWrgti6CsZAgMBAAECgYEAop6ZfNY4RMhHbDtPdhr6Ttl2/z3RPYlmwnd8mQyyG6nhYmxIDlRNztSDBTpdaDOEYmwzgnPL1Nv/R/vIA8aEMKS7o42bJBH6Ghfs1gNHtQJxWnVfg4xx34wE3kSaEaBcF5+GC4DFGXTfrKvUgmzXdUS5GILoHlyhSAGuyAIuR6kCQQDRUlw/YjHOnqITcywG1/H06zLjjXhW/KLXgtZ3QTBGtYzoCSSaA9GFqg+wiYhUhsTvISEmHrrF04yHtQSNYfALAkEAzaHmITY5hzk2jYzN41gAZY2f4Ie7EjI3OhyfToXr431WjR+PEfnCG34T7qwna7JlCRS3qeUBoVxI99RfTZAT6wJAZxZ8v+qIRQ3zHEkYYbZwPwKmaVL+9TguqpyRKuI7+FbpMk1ubTt8MKv0ViklrxtAWdSjErcHnO32w87ukwKsyQJAPcfIhm3uNsxfsYN+UDGeXOSCBkmBUOQ5Z4I18reVuZFssZWrvHDXer8Uaf7bESZV6LaKv8aICNzbJ2ps/7ar5QJAG1VeCmN3Q7MNTL71tG1BtGz+2W1pwDy9NwuDLTjrmga7kKA1JOAPKU4CQg95YOtl+q12xPl1iFExptq2tQH8Kw==";
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
}
