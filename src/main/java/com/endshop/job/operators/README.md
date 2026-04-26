# 干员系统说明

## 目录结构

```
operators/
├── OperatorManager.java              # 干员管理器(统一接口)
├── OperatorProfileManager.java       # 档案管理器(JSON加载)
├── [OperatorName]Profile.java        # 干员配置类(Java)
└── [OperatorName]Skills.java         # 干员技能组(Java)

data/endshopattribute/operators/
├── _template.json                    # 档案模板
├── yvon.json                         # 伊冯档案
├── etera.json                        # 埃特拉档案
└── ...                               # 其他干员档案
```

## 文件说明

### 1. Java配置类 (Profile & Skills)
每个干员的独立配置和技能注册文件。

### 2. JSON档案文件
详细的干员档案,包含:
- 基本信息(名称、职业、稀有度)
- 能力值配置
- 技能列表
- 基础属性(生命值、攻击力、防御力等)
- 背景故事

## 使用方法

### 方式1: 使用Java配置类
```java
// 直接访问静态字段
int strength = YvonProfile.BASE_STRENGTH;
String[] skills = YvonProfile.SKILLS;
```

### 方式2: 使用JSON档案
```java
// 获取干员名称
String name = OperatorProfileManager.getOperatorName("yvon");

// 获取干员职业
String profession = OperatorProfileManager.getOperatorProfession("yvon");

// 获取干员描述
String desc = OperatorProfileManager.getOperatorDescription("yvon");
```

### 添加新干员

**步骤1**: 创建Java配置类
1. 复制 `_template.json` 为 `[operator_id].json`
2. 填写干员信息
3. 创建 `[OperatorName]Profile.java` 和 `[OperatorName]Skills.java`

**步骤2**: 在 `OperatorManager.registerAll()` 中注册

## 已创建的干员 (16个)

### 近卫 (GUARD) - 14个
1. 伊冯 (Yvon)
2. 佩丽卡 (Perica)
3. 史尔特尔 (Surtr)
4. 洁尔佩塔 (Gerpeita)
5. 洛茜 (Rosie)
6. 管理员B (Admin B)
7. 陈千语 (Chen Qianyu)
8. 别礼 (Bieli)
9. 庄方宜 (Zhuang Fangyi)
10. 汤汤 (Tangtang)
11. 秋栗 (Qiuli)
12. 管理员A (Admin A)
13. 萤石 (Yingshi)
14. 赛希 (Saixi)

### 先锋 (VANGUARD) - 1个
15. 埃特拉 (Etera)

## 维护建议

1. **修改干员属性**: 直接编辑对应的 Profile 文件
2. **修改干员技能**: 编辑 Profile 文件中的 SKILLS 数组
3. **添加新干员**: 按照模板创建新的 Profile 和 Skills 文件
4. **查询干员信息**: 使用 OperatorManager 提供的接口

## 注意事项

- 所有干员档案必须实现 `OperatorManager.OperatorProfile` 接口
- 干员ID必须与实体注册ID一致
- 技能ID必须在 SkillRegistry 中已注册
