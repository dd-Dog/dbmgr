package com.example.dbmgr.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.dbmgr.db.DbConstants;
import com.example.dbmgr.db.ShopInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by bianjb on 2017/8/9.
 */

public class ArraysUtil {

    public static List<String> sort(List<String> src) {
        String[] arr = src.toArray(new String[src.size()]);
        Arrays.sort(arr, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                char c1 = s1.charAt(0);
                char c2 = s2.charAt(0);
                return c1 - c2;
            }
        });
        src = Arrays.asList(arr);
        return src;
    }

    /**
     * 对比是否相同
     *
     * @param list1
     * @param list2
     * @return
     */
    public static int compare(List<String> list1, List<String> list2) {
        int isEqaul = DbConstants.DB_EQUAL;
        if (list1.size() != list2.size()) {
            isEqaul = DbConstants.DB_NOT_EQAUL;
        } else if (list1.size() == 0) {
            isEqaul = DbConstants.DB_EMPTY;
        } else {
            for (int i = 0; i < list1.size(); i++) {
                if (!TextUtils.equals(list1.get(i), list2.get(i))) {
                    isEqaul = DbConstants.DB_NOT_EQAUL;
                }
            }
        }
        return isEqaul;
    }

    /**
     * @param base   要改动的数据
     * @param append 要添加的数据
     * @return
     */
    public static ArrayList<ShopInfo> union(ArrayList<ShopInfo> base, ArrayList<ShopInfo> append) {
        for (int i = 0; i < append.size(); i++) {
            ShopInfo shopInfo = append.get(i);
            if (base.contains(shopInfo)) {
                continue;
            } else {
                int index = hasSerail(base, shopInfo.serial);
                if (index != -1) {
                    //如果存在相同serial则更新
                    base.remove(index);
                    base.add(shopInfo);
                }else {
                    //不存在则添加
                    base.add(shopInfo);
                }
            }
        }
        return base;
    }

    private static int hasSerail(ArrayList<ShopInfo> base, String serial) {
        if (base == null) return -1;
        for (int i = 0; i < base.size(); i++) {
            if (TextUtils.equals(serial, base.get(i).serial)) {
                return i;
            }
        }
        return -1;
    }
}
