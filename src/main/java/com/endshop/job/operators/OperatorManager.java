package com.endshop.job.operators;

import java.util.HashMap;
import java.util.Map;

/**
 * 干员管理器 - 统一管理所有干员的档案和技能
 * 
 * 注意: 每个干员的配置直接在其Profile类中定义,
 * 通过静态字段访问,无需实现接口
 */
public class OperatorManager {
    
    /**
     * 注册所有干员(用于初始化)
     */
    public static void registerAll() {
        System.out.println("[干员管理器] 已准备 16 个干员");
        System.out.println("[干员管理器] 近卫: 伊冯、佩丽卡、史尔特尔、洁尔佩塔、洛茜、管理员B、陈千语、别礼、庄方宜、汤汤、秋栗、管理员A、萤石、赛希");
        System.out.println("[干员管理器] 先锋: 埃特拉");
    }
}
