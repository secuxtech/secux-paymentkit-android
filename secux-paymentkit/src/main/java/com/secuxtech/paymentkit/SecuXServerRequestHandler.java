package com.secuxtech.paymentkit;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-10
 */

import android.util.Log;
import androidx.core.util.Pair;

import org.json.JSONObject;


public class SecuXServerRequestHandler extends RestRequestHandler {

    static String baseURL = "https://pmsweb-sandbox.secuxtech.com";
    static final String adminLoginUrl = baseURL + "/api/Admin/Login";
    static final String registerUrl = baseURL + "/api/Consumer/Register";
    static final String userLoginUrl = baseURL + "/api/Consumer/Login";
    static final String changePwdUrl = baseURL + "/api/Consumer/ChangePassword";
    static final String transferUrl = baseURL + "/api/Consumer/Transfer";
    static final String balanceUrl = baseURL + "/api/Consumer/GetAccountBalance";
    static final String balanceListUrl = baseURL + "/api/Consumer/GetAccountBalanceList";
    static final String paymentUrl = baseURL + "/api/Consumer/Payment";
    static final String paymentHistoryUrl = baseURL + "/api/Consumer/GetPaymentHistory";
    static final String getStoreUrl = baseURL + "/api/Terminal/GetStore";
    static final String transferHistoryUrl = baseURL + "/api/Consumer/GetTxHistory";
    static final String getDeviceInfoUrl = baseURL + "/api/Terminal/GetDeviceInfo";
    static final String getSupportedSymbolUrl = baseURL + "/api/Terminal/GetSupportedSymbol";
    static final String getChainAccountListUrl = baseURL + "/api/Consumer/GetChainAccountList";

    private static String mToken = "";

    public String getAdminToken(){
        Log.i(TAG, "getAdminToken");
        try {
            JSONObject param = new JSONObject();
            param.put("account", "secux_register");
            param.put("password", "!secux_register@123");
            Pair<Integer, String> response = this.processPostRequest(adminLoginUrl, param);

            if (response.first == SecuXRequestOK) {
                JSONObject responseJson = new JSONObject(response.second);
                String token = responseJson.getString("token");
                return token;
            }

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

        return "";
    }

    public Pair<Integer, String> userRegister(SecuXUserAccount userAccount, String coinType, String token){
        Log.i(TAG, "userRegister");
        String adminToken = getAdminToken();

        if (adminToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(SecuXRequestFailed, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("account", userAccount.mAccountName);
            param.put("password", userAccount.mPassword);
            param.put("email", userAccount.mEmail);
            param.put("alias", userAccount.mAlias);
            param.put("tel", userAccount.mPhoneNum);
            param.put("coinType", coinType);
            param.put("symbol", token);
            param.put("optional", "{}");

            Pair<Integer, String> result = this.processPostRequest(registerUrl, param, adminToken, 30000);

            Log.i(TAG, result.second);
            return result;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }
    }

    public Pair<Integer, String> changePassword(String oldPwd, String newPwd){
        Log.i(TAG, "changePassword");

        try{
            JSONObject param = new JSONObject();
            param.put("password", oldPwd);
            param.put("newPassword", newPwd);
            Pair<Integer, String> response = this.processPostRequest(changePwdUrl, param, mToken);
            if (response.first==SecuXRequestOK){
                //JSONObject responseJson = new JSONObject(response.second);
                //String token = responseJson.getString("token");
                //mToken = token;
            }

            Log.i(TAG, response.second);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());

            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }
    }

    public Pair<Integer, String> userLogin(String account, String pwd){
        Log.i(TAG, "userLogin");

        try{
            JSONObject param = new JSONObject();
            param.put("account", account);
            param.put("password", pwd);
            Pair<Integer, String> response = this.processPostRequest(userLoginUrl, param);
            if (response.first==SecuXRequestOK){
                JSONObject responseJson = new JSONObject(response.second);
                String token = responseJson.getString("token");
                mToken = token;
            }

            Log.i(TAG, response.second);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());

            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }
    }

    public Pair<Integer, String> getSupportedCoinTokens(){
        Log.i(TAG, "getSupportedCoinTokens");

        return this.processPostRequest(getSupportedSymbolUrl);
    }

    public Pair<Integer, String> getChainAccountList(){
        Log.i(TAG, "getChainAccountList");

        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(SecuXRequestFailed, "No token");
        }

        return this.processPostRequest(getChainAccountListUrl, null, mToken);
    }

    public Pair<Integer, String> getAccountBalance(String cointype, String token){
        Log.i(TAG, "getAccountBalance " + cointype + " " + token);

        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(SecuXRequestFailed, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            //param.put("account", account);
            param.put("coinType", cointype);
            param.put("symbol", token);
            Pair<Integer, String> response = this.processPostRequest(balanceUrl, param, mToken);

            Log.i(TAG, response.second);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }
    }

    /*
    public Pair<Integer, String> getAccountBalance(){
        Log.i(TAG, "getAccountBalance ");

        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(SecuXRequestFailed, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            //param.put("account", account);
            Pair<Integer, String> response = this.processPostRequest(balanceListUrl, param, mToken);

            Log.i(TAG, response.second);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }
    }


     */
    public Pair<Integer, String> getStoreInfo(String devID){
        Log.i(TAG, "getStoreInfo");

        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(SecuXRequestFailed, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("deviceIDhash", devID);

            Pair<Integer, String> response = this.processPostRequest(getStoreUrl, param, mToken);

            Log.i(TAG, response.second);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }

    }

    public Pair<Integer, String> doPayment(String sender, String storeName, PaymentInfo payInfo){
        Log.i(TAG, "doPayment");
        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(SecuXRequestFailed, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("ivKey", payInfo.mIVKey);
            param.put("memo", "");
            param.put("symbol", payInfo.mToken);
            param.put("amount", payInfo.mAmount);
            param.put("coinType", payInfo.mCoinType);
            //param.put("account", sender);
            param.put("receiver", payInfo.mDevID);

            Pair<Integer, String> response = this.processPostRequest(paymentUrl, param, mToken, 15000);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }
    }

    public Pair<Integer, String> doTransfer(String cointype, String token, String feesymbol, String receiver, String amount){
        Log.i(TAG, "doTransfer");
        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(SecuXRequestFailed, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("coinType", cointype);
            param.put("symbol", token);
            param.put("feeSymbol", feesymbol);
            //param.put("account", account);
            param.put("receiver", receiver);
            param.put("amount", amount);

            Pair<Integer, String> response = this.processPostRequest(transferUrl, param, mToken, 15000);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }
    }

    public Pair<Integer, String> getPaymentHistory(String token, String transactionCode){
        Log.i(TAG, "getPaymentHistory " + token + " " + transactionCode);
        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(SecuXRequestFailed, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            //param.put("account", account.mAccountName);
            param.put("symbol", token);
            param.put("page", 1);
            param.put("count", 10);
            param.put("columnName", "");
            param.put("sorting", "");
            param.put("transactionCode", transactionCode);

            Pair<Integer, String> response = this.processPostRequest(paymentHistoryUrl, param, mToken);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }
    }

    public Pair<Integer, String> getPaymentHistory(String token, int pageIdx, int pageItemCount){
        Log.i(TAG, "getPaymentHistory");
        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(SecuXRequestFailed, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            //param.put("account", account.mAccountName);
            param.put("symbol", token);
            param.put("page", pageIdx);
            param.put("count", pageItemCount);
            param.put("columnName", "");
            param.put("sorting", "");

            Pair<Integer, String> response = this.processPostRequest(paymentHistoryUrl, param, mToken);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }
    }

    public Pair<Integer, String> getTransferHistory(String cointype, String symboltype, int page, int count){
        Log.i(TAG, "getTransferHistory");
        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(SecuXRequestFailed, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            //param.put("account", account.mAccountName);
            param.put("coinType", cointype);
            param.put("symbol", symboltype);
            param.put("page", page);
            param.put("count", count);

            Pair<Integer, String> response = this.processPostRequest(transferHistoryUrl, param, mToken);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }
    }

    /*
    public Pair<Integer, String> getDeviceInfo(String coinType, String token, String amount, String deviceID){
        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(SecuXRequestFailed, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            //param.put("account", account.mAccountName);
            param.put("coinType", coinType);
            param.put("symbol", token);
            param.put("amount", amount);
            param.put("deviceID", deviceID);

            Pair<Integer, String> response = this.processPostRequest(getDeviceInfoUrl, param, mToken);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }
    }
    */

    public Pair<Integer, String> getDeviceInfo(String paymentInfo){
        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(SecuXRequestFailed, "No token");
        }

        try{
            JSONObject param = new JSONObject(paymentInfo);
            Pair<Integer, String> response = this.processPostRequest(getDeviceInfoUrl, param, mToken);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(SecuXRequestFailed, e.getLocalizedMessage());
        }
    }
}
