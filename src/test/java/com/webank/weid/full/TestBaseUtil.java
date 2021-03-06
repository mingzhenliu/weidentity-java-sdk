/*
 *       Copyright© (2018) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.full;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.request.UpdateCptArgs;
import com.webank.weid.protocol.request.VerifyCredentialArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;

/**
 * testing basic entity object building classes.
 * 
 * @author v_wbgyang
 *
 */
public class TestBaseUtil {
    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(TestBaseUtil.class);

    /**
     * build VerifyCredentialArgs.
     */
    public static VerifyCredentialArgs buildVerifyCredentialArgs(
        Credential credential,
        String publicKey) {

        VerifyCredentialArgs verifyCredentialArgs = new VerifyCredentialArgs();
        verifyCredentialArgs.setCredential(credential);
        verifyCredentialArgs.setWeIdPublicKey(new WeIdPublicKey());
        verifyCredentialArgs.getWeIdPublicKey().setPublicKey(publicKey);
        return verifyCredentialArgs;
    }

    /**
     * build CreateCredentialArgs no cptId.
     */
    public static CreateCredentialArgs buildCreateCredentialArgs(CreateWeIdDataResult createWeId) {

        CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
        createCredentialArgs.setIssuer(createWeId.getWeId());
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() + (1000 * 60 * 60 * 24));
        createCredentialArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        createCredentialArgs.getWeIdPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());
        createCredentialArgs.setClaim(TestData.schemaData);

        return createCredentialArgs;
    }

    /**
     * build default CreateCredentialArgs.
     */
    public static CreateCredentialArgs buildCreateCredentialArgs(
        CreateWeIdDataResult createWeId,
        CptBaseInfo cptBaseInfo) {

        CreateCredentialArgs createCredentialArgs = buildCreateCredentialArgs(createWeId);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());
        return createCredentialArgs;
    }

    /**
     * build default UpdateCptArgs.
     */
    public static UpdateCptArgs buildUpdateCptArgs(
        CreateWeIdDataResult createWeId,
        CptBaseInfo cptBaseInfo) {

        UpdateCptArgs updateCptArgs = new UpdateCptArgs();
        updateCptArgs.setCptJsonSchema(TestData.schema);
        updateCptArgs.setCptPublisher(createWeId.getWeId());
        updateCptArgs.setCptPublisherPrivateKey(new WeIdPrivateKey());
        updateCptArgs.getCptPublisherPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());
        updateCptArgs.setCptId(cptBaseInfo.getCptId());

        return updateCptArgs;
    }

    /**
     * build default RegisterCptArgs.
     */
    public static RegisterCptArgs buildRegisterCptArgs(CreateWeIdDataResult createWeId) {

        RegisterCptArgs registerCptArgs = new RegisterCptArgs();
        registerCptArgs.setCptJsonSchema(TestData.schema);
        registerCptArgs.setCptPublisher(createWeId.getWeId());
        registerCptArgs.setCptPublisherPrivateKey(new WeIdPrivateKey());
        registerCptArgs.getCptPublisherPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());

        return registerCptArgs;
    }

    /**
     * build default RegisterAuthorityIssuerArgs.
     */
    public static RegisterAuthorityIssuerArgs buildRegisterAuthorityIssuerArgs(
        CreateWeIdDataResult createWeId, 
        String privateKey) {

        AuthorityIssuer authorityIssuer = new AuthorityIssuer();
        authorityIssuer.setWeId(createWeId.getWeId());
        authorityIssuer.setCreated(new Date().getTime());
        authorityIssuer.setName(TestData.authorityIssuerName);
        authorityIssuer.setAccValue(TestData.authorityIssuerAccValue);

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = new RegisterAuthorityIssuerArgs();
        registerAuthorityIssuerArgs.setAuthorityIssuer(authorityIssuer);
        registerAuthorityIssuerArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(privateKey);

        return registerAuthorityIssuerArgs;
    }

    /**
     * build default CreateWeIdArgs.
     * 
     */
    public static CreateWeIdArgs buildCreateWeIdArgs() {
        CreateWeIdArgs args = new CreateWeIdArgs();
        PasswordKey passwordKey = createEcKeyPair();
        args.setPublicKey(passwordKey.getPublicKey());

        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(passwordKey.getPrivateKey());

        args.setWeIdPrivateKey(weIdPrivateKey);

        return args;
    }

    /**
     * buildSetPublicKeyArgs.
     */
    public static SetAuthenticationArgs buildSetAuthenticationArgs(
        CreateWeIdDataResult createWeId) {

        SetAuthenticationArgs setAuthenticationArgs = new SetAuthenticationArgs();
        setAuthenticationArgs.setWeId(createWeId.getWeId());
        setAuthenticationArgs.setPublicKey(createWeId.getUserWeIdPublicKey().getPublicKey());
        setAuthenticationArgs.setType(TestData.authenticationType);
        setAuthenticationArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setAuthenticationArgs.getUserWeIdPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());

        return setAuthenticationArgs;
    }

    /**
     * buildSetPublicKeyArgs.
     */
    public static SetPublicKeyArgs buildSetPublicKeyArgs(CreateWeIdDataResult createWeId) {

        SetPublicKeyArgs setPublicKeyArgs = new SetPublicKeyArgs();
        setPublicKeyArgs.setWeId(createWeId.getWeId());
        setPublicKeyArgs.setPublicKey(createWeId.getUserWeIdPublicKey().getPublicKey());
        setPublicKeyArgs.setType(TestData.publicKeyType);
        setPublicKeyArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setPublicKeyArgs.getUserWeIdPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());

        return setPublicKeyArgs;
    }

    /**
     * buildSetPublicKeyArgs.
     */
    public static SetServiceArgs buildSetServiceArgs(CreateWeIdDataResult createWeId) {

        SetServiceArgs setServiceArgs = new SetServiceArgs();
        setServiceArgs.setWeId(createWeId.getWeId());
        setServiceArgs.setType(TestData.serviceType);
        setServiceArgs.setServiceEndpoint(TestData.serviceEndpoint);
        setServiceArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setServiceArgs.getUserWeIdPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());

        return setServiceArgs;
    }

    /**
     * buildRemoveAuthorityIssuerArgs.
     */
    public static RemoveAuthorityIssuerArgs buildRemoveAuthorityIssuerArgs(
        CreateWeIdDataResult createWeId, 
        String privateKey) {

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs = new RemoveAuthorityIssuerArgs();
        removeAuthorityIssuerArgs.setWeId(createWeId.getWeId());
        removeAuthorityIssuerArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        removeAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(privateKey);

        return removeAuthorityIssuerArgs;
    }

    /**
     * create a new public key - private key.
     * 
     */
    public static PasswordKey createEcKeyPair() {

        PasswordKey passwordKey = new PasswordKey();
        try {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            String publicKey = String.valueOf(keyPair.getPublicKey());
            String privateKey = String.valueOf(keyPair.getPrivateKey());
            passwordKey.setPrivateKey(privateKey);
            passwordKey.setPublicKey(publicKey);
            BeanUtil.print(passwordKey);
        } catch (InvalidAlgorithmParameterException e) {
            logger.error("createEcKeyPair error:", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("createEcKeyPair error:", e);
        } catch (NoSuchProviderException e) {
            logger.error("createEcKeyPair error:", e);
        }
        return passwordKey;
    }

    /**
     * to test the public and private key from the file.
     * 
     * @param fileName fileName
     * @return
     */
    public static String[] resolvePk(String fileName) {

        BufferedReader br = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;

        try {

            URL fileUrl = TestBaseUtil.class.getClassLoader().getResource(fileName);
            if (null == fileUrl) {
                return null;
            }

            String filePath = fileUrl.getFile();
            if (null == filePath) {
                return null;
            }

            fis = new FileInputStream(fileUrl.getFile());
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            List<String> strList = new ArrayList<String>();
            String line = null;
            while ((line = br.readLine()) != null) {
                strList.add(line);
            } 

            String[] pk = new String[2];
            for (int i = 0; i < strList.size(); i++) {
                String str = strList.get(i);
                if (StringUtils.isBlank(str)) {
                    continue;
                }
                String[] lineStr = str.split(":");

                if (lineStr.length == 2) {
                    pk[i] = lineStr[1];
                }
            }

            logger.info("publicKey:" + pk[0]);
            logger.info("privateKey:" + pk[1]);
            return pk;
        } catch (FileNotFoundException e) {
            logger.error("resolvePk error:",e);
        } catch (IOException e) {
            logger.error("resolvePk error:",e);
        }  finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error("br close error:",e);
                }
            }
            if (null != isr) {
                try {
                    isr.close();
                } catch (IOException e) {
                    logger.error("isr close error:",e);
                }
            }
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("fis close error:",e);
                }
            }
        }
        return null;
    }
}
