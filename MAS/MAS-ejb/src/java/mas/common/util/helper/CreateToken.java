/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.util.helper;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 *
 * @author winga_000
 */
public class CreateToken {
    public static String createNewToken() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
