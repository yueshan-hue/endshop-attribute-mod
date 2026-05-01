package com.endshop.job;

import com.endshop.job.command.SetJobCommand;
import com.endshop.job.command.AdminTaskCommand;
import com.endshop.job.config.ModConfig;
import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.entity.EndshopEntity;
import com.endshop.job.entity.EndshopEntityTypes;
import com.endshop.job.item.JobBookItem;
import com.endshop.job.item.OperatorArtItem;
import com.endshop.job.item.ErosionBucketItem;
import com.endshop.job.skill.SkillDataAttachment;
import com.endshop.job.skill.SkillInitializer;
import com.endshop.job.operators.OperatorProfileManager;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(EndshopJob.MODID)
public class EndshopJob {

    public static final String MODID = "endshopattribute";
    public static final Logger LOGGER = LogUtils.getLogger();

    // 物品注册表
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // 创造标签注册表
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 职业书物品
    public static final DeferredItem<Item> JOB_BOOK = 
            ITEMS.register("job_book", () -> new JobBookItem(new Item.Properties().stacksTo(1)));

    // 庄方宜美图物品
    public static final DeferredItem<Item> ZHUANG_FANGYI_ART_ZONG_XIANG_YI = 
            ITEMS.register("zhuang_fangyi_art_zong_xiang_yi", () -> new OperatorArtItem(
                    new Item.Properties().stacksTo(1), 
                    "庄方宜", 
                    "总相宜", 
                    "Momoku"));
    
    public static final DeferredItem<Item> ZHUANG_FANGYI_ART_YU_TING_DE_SHUN_JIAN = 
            ITEMS.register("zhuang_fangyi_art_yu_ting_de_shun_jian", () -> new OperatorArtItem(
                    new Item.Properties().stacksTo(1), 
                    "庄方宜", 
                    "雨停的瞬间", 
                    "KU"));
    
    public static final DeferredItem<Item> ZHUANG_FANGYI_ART_DA_JU_YI_DING = 
            ITEMS.register("zhuang_fangyi_art_da_ju_yi_ding", () -> new OperatorArtItem(
                    new Item.Properties().stacksTo(1), 
                    "庄方宜", 
                    "大局已定", 
                    "LM7"));

    // 射流无人机物品
    public static final DeferredItem<Item> JET_DRONE_ITEM = 
            ITEMS.register("jet_drone", () -> new Item(new Item.Properties().stacksTo(64)));

    // 侵蚀流体桶
    public static final DeferredItem<Item> EROSION_BUCKET = 
            ITEMS.register("erosion_bucket", ErosionBucketItem::new);

    // 侵蚀核心掉落物
    public static final DeferredItem<Item> EROSION_CORE_ITEM = 
            ITEMS.register("erosion_core_item", () -> new Item(new Item.Properties().stacksTo(64)));

    // ===== 混沌系统物品 =====
    
    // 混沌精华（4种稀有度）
    public static final DeferredItem<Item> CHAOS_ESSENCE_COMMON = 
            ITEMS.register("chaos_essence_common", () -> new com.endshop.job.item.chaos.ChaosEssenceItem(
                new Item.Properties().stacksTo(64), 1, com.endshop.job.item.chaos.ChaosEssenceItem.EssenceRarity.COMMON));
    
    public static final DeferredItem<Item> CHAOS_ESSENCE_UNCOMMON = 
            ITEMS.register("chaos_essence_uncommon", () -> new com.endshop.job.item.chaos.ChaosEssenceItem(
                new Item.Properties().stacksTo(64), 2, com.endshop.job.item.chaos.ChaosEssenceItem.EssenceRarity.UNCOMMON));
    
    public static final DeferredItem<Item> CHAOS_ESSENCE_RARE = 
            ITEMS.register("chaos_essence_rare", () -> new com.endshop.job.item.chaos.ChaosEssenceItem(
                new Item.Properties().stacksTo(64), 3, com.endshop.job.item.chaos.ChaosEssenceItem.EssenceRarity.RARE));
    
    public static final DeferredItem<Item> CHAOS_ESSENCE_LEGENDARY = 
            ITEMS.register("chaos_essence_legendary", () -> new com.endshop.job.item.chaos.ChaosEssenceItem(
                new Item.Properties().stacksTo(64), 5, com.endshop.job.item.chaos.ChaosEssenceItem.EssenceRarity.LEGENDARY));
    
    // 混沌提取器
    public static final DeferredItem<Item> CHAOS_EXTRACTOR = 
            ITEMS.register("chaos_extractor", () -> new com.endshop.job.item.chaos.ChaosExtractorItem(
                new Item.Properties().stacksTo(1)));
    
    // 混沌强化器（3种类型）
    public static final DeferredItem<Item> CHAOS_ENHANCER_RARITY = 
            ITEMS.register("chaos_enhancer_rarity", () -> new com.endshop.job.item.chaos.ChaosEnhancerItem(
                new Item.Properties(), com.endshop.job.item.chaos.ChaosEnhancerItem.EnhanceType.RARITY_UPGRADE));
    
    public static final DeferredItem<Item> CHAOS_ENHANCER_AFFIX = 
            ITEMS.register("chaos_enhancer_affix", () -> new com.endshop.job.item.chaos.ChaosEnhancerItem(
                new Item.Properties(), com.endshop.job.item.chaos.ChaosEnhancerItem.EnhanceType.AFFIX_UPGRADE));
    
    public static final DeferredItem<Item> CHAOS_ENHANCER_REFORGE = 
            ITEMS.register("chaos_enhancer_reforge", () -> new com.endshop.job.item.chaos.ChaosEnhancerItem(
                new Item.Properties(), com.endshop.job.item.chaos.ChaosEnhancerItem.EnhanceType.REFORGE));

    // ===== 珍贵物品（23个） =====
    
    // 抽卡券（2个）
    public static final DeferredItem<Item> GACHA_TICKET_STANDARD = 
            ITEMS.register("gacha_ticket_standard", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> GACHA_TICKET_SPECIAL = 
            ITEMS.register("gacha_ticket_special", () -> new Item(new Item.Properties().stacksTo(64)));
    
    // 凭证类物品（14个）
    public static final DeferredItem<Item> VOUCHER_BASIC = 
            ITEMS.register("voucher_basic", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_GUARANTEE = 
            ITEMS.register("voucher_guarantee", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_INTEGRATED = 
            ITEMS.register("voucher_integrated", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_JOURNEY = 
            ITEMS.register("voucher_journey", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_LIMITED_COLORFUL = 
            ITEMS.register("voucher_limited_colorful", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_LIMITED_FIRE = 
            ITEMS.register("voucher_limited_fire", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_LIMITED_MESSENGER = 
            ITEMS.register("voucher_limited_messenger", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_LIMITED_SPRING = 
            ITEMS.register("voucher_limited_spring", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_LIMITED_WAVE = 
            ITEMS.register("voucher_limited_wave", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_LIMITED_WEAPON = 
            ITEMS.register("voucher_limited_weapon", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_LIMITED_WOLF = 
            ITEMS.register("voucher_limited_wolf", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_SPECIAL_10 = 
            ITEMS.register("voucher_special_10", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_TERMINUS = 
            ITEMS.register("voucher_terminus", () -> new Item(new Item.Properties().stacksTo(64)));
    
    // 芯片（3个）
    public static final DeferredItem<Item> CHIP_BASIC = 
            ITEMS.register("chip_basic", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> CHIP_ADVANCED = 
            ITEMS.register("chip_advanced", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> CHIP_SUPREME = 
            ITEMS.register("chip_supreme", () -> new Item(new Item.Properties().stacksTo(64)));
    
    // 精英材料（3个）
    public static final DeferredItem<Item> ELITE_MATERIAL_1 = 
            ITEMS.register("elite_material_1", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> ELITE_MATERIAL_2 = 
            ITEMS.register("elite_material_2", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> ELITE_MATERIAL_3 = 
            ITEMS.register("elite_material_3", () -> new Item(new Item.Properties().stacksTo(64)));
    
    // 经验书（3个）
    public static final DeferredItem<Item> EXP_BOOK_LOW = 
            ITEMS.register("exp_book_low", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> EXP_BOOK_MID = 
            ITEMS.register("exp_book_mid", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> EXP_BOOK_HIGH = 
            ITEMS.register("exp_book_high", () -> new Item(new Item.Properties().stacksTo(64)));
    
    // 战术手册（3个）
    public static final DeferredItem<Item> TACTICAL_MANUAL_LOW = 
            ITEMS.register("tactical_manual_low", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> TACTICAL_MANUAL_MID = 
            ITEMS.register("tactical_manual_mid", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> TACTICAL_MANUAL_HIGH = 
            ITEMS.register("tactical_manual_high", () -> new Item(new Item.Properties().stacksTo(64)));
    
    // 技能概要（3个）
    public static final DeferredItem<Item> SKILL_SUMMARY_1 = 
            ITEMS.register("skill_summary_1", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> SKILL_SUMMARY_2 = 
            ITEMS.register("skill_summary_2", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> SKILL_SUMMARY_3 = 
            ITEMS.register("skill_summary_3", () -> new Item(new Item.Properties().stacksTo(64)));
    
    // 其他珍贵物品（6个）
    public static final DeferredItem<Item> ORIROCK_CUBE = 
            ITEMS.register("orirock_cube", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> ORORIGINIUM_DERIVATIVE = 
            ITEMS.register("ororiginium_derivative", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> POTENTIAL_SHARD = 
            ITEMS.register("potential_shard", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> TRACE_SURVIVAL = 
            ITEMS.register("trace_survival", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_SPECIAL = 
            ITEMS.register("voucher_special", () -> new Item(new Item.Properties().stacksTo(64)));
    
    public static final DeferredItem<Item> VOUCHER_STAR = 
            ITEMS.register("voucher_star", () -> new Item(new Item.Properties().stacksTo(64)));

    // ===== 终末地武器系统 =====

    // ===== 单手剑（19把） =====
    public static final DeferredItem<Item> WEAPON_O_B_J_LIGHT_EDGE =
            ITEMS.register("o_b_j_light_edge", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_NO_RETURN =
            ITEMS.register("no_return", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_LOOK_UP =
            ITEMS.register("look_up", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_GLORY_MEMORY =
            ITEMS.register("glory_memory", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_TWELVE_QUESTIONS =
            ITEMS.register("twelve_questions", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_FORTRESS_FORGER =
            ITEMS.register("fortress_forger", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_TAR11 =
            ITEMS.register("tar11", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_GREAT_WISH =
            ITEMS.register("great_wish", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_SOARING =
            ITEMS.register("soaring", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_FAMOUS_REPUTATION =
            ITEMS.register("famous_reputation", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_SHARP_EDGE =
            ITEMS.register("sharp_edge", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_WAVE =
            ITEMS.register("wave", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_THERMAL_CUTTER =
            ITEMS.register("thermal_cutter", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_MOLTEN_FLAME =
            ITEMS.register("molten_flame", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_WOLF_CRIMSON =
            ITEMS.register("wolf_crimson", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_WHITE_NIGHT_STAR =
            ITEMS.register("white_night_star", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_SCALE_CHASER3_0 =
            ITEMS.register("scale_chaser3_0", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_STEEL_ECHO =
            ITEMS.register("steel_echo", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    public static final DeferredItem<Item> WEAPON_DARK_TORCH =
            ITEMS.register("dark_torch", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.SINGLE_SWORD, 10, 1, 1.6f));

    // ===== 双手剑（12把） =====
    public static final DeferredItem<Item> WEAPON_O_B_J_HEAVY_LOAD =
            ITEMS.register("o_b_j_heavy_load", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.TWO_HANDED_SWORD, 15, 2, 1.2f));

    public static final DeferredItem<Item> WEAPON_PARAGON =
            ITEMS.register("paragon", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.TWO_HANDED_SWORD, 15, 2, 1.2f));

    public static final DeferredItem<Item> WEAPON_ANCIENT_CHANNEL =
            ITEMS.register("ancient_channel", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.TWO_HANDED_SWORD, 15, 2, 1.2f));

    public static final DeferredItem<Item> WEAPON_THUNDER_SPOT =
            ITEMS.register("thunder_spot", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.TWO_HANDED_SWORD, 15, 2, 1.2f));

    public static final DeferredItem<Item> WEAPON_INDUSTRY_ZERO_ONE =
            ITEMS.register("industry_zero_one", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.TWO_HANDED_SWORD, 15, 2, 1.2f));

    public static final DeferredItem<Item> WEAPON_DRAGON_SEEKER =
            ITEMS.register("dragon_seeker", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.TWO_HANDED_SWORD, 15, 2, 1.2f));

    public static final DeferredItem<Item> WEAPON_PAST_MASTERPIECE =
            ITEMS.register("past_masterpiece", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.TWO_HANDED_SWORD, 15, 2, 1.2f));

    public static final DeferredItem<Item> WEAPON_QUENCHER =
            ITEMS.register("quencher", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.TWO_HANDED_SWORD, 15, 2, 1.2f));

    public static final DeferredItem<Item> WEAPON_BROKEN_KING =
            ITEMS.register("broken_king", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.TWO_HANDED_SWORD, 15, 2, 1.2f));

    public static final DeferredItem<Item> WEAPON_FINAL_VOICE =
            ITEMS.register("final_voice", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.TWO_HANDED_SWORD, 15, 2, 1.2f));

    public static final DeferredItem<Item> WEAPON_HRAFEN =
            ITEMS.register("hrafen", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.TWO_HANDED_SWORD, 15, 2, 1.2f));

    public static final DeferredItem<Item> WEAPON_DARHOF7 =
            ITEMS.register("darhof7", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.TWO_HANDED_SWORD, 15, 2, 1.2f));

    // ===== 手铳（12把） =====
    public static final DeferredItem<Item> WEAPON_O_B_J_SWIFT_EXTREME =
            ITEMS.register("o_b_j_swift_extreme", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.PISTOL, 12, 1, 2.0f));

    public static final DeferredItem<Item> WEAPON_WORK_ALL_BEINGS =
            ITEMS.register("work_all_beings", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.PISTOL, 12, 1, 2.0f));

    public static final DeferredItem<Item> WEAPON_PEKO5 =
            ITEMS.register("peko5", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.PISTOL, 12, 1, 2.0f));

    public static final DeferredItem<Item> WEAPON_CANNIBALISM =
            ITEMS.register("cannibalism", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.PISTOL, 12, 1, 2.0f));

    public static final DeferredItem<Item> WEAPON_HOWLING_GUARD =
            ITEMS.register("howling_guard", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.PISTOL, 12, 1, 2.0f));

    public static final DeferredItem<Item> WEAPON_HOMESICK =
            ITEMS.register("homesick", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.PISTOL, 12, 1, 2.0f));

    public static final DeferredItem<Item> WEAPON_WEDGE =
            ITEMS.register("wedge", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.PISTOL, 12, 1, 2.0f));

    public static final DeferredItem<Item> WEAPON_RATIONAL_FAREWELL =
            ITEMS.register("rational_farewell", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.PISTOL, 12, 1, 2.0f));

    public static final DeferredItem<Item> WEAPON_ART_TYRANT =
            ITEMS.register("art_tyrant", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.PISTOL, 12, 1, 2.0f));

    public static final DeferredItem<Item> WEAPON_FALL_GRASS =
            ITEMS.register("fall_grass", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.PISTOL, 12, 1, 2.0f));

    public static final DeferredItem<Item> WEAPON_LONG_ROAD =
            ITEMS.register("long_road", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.PISTOL, 12, 1, 2.0f));

    public static final DeferredItem<Item> WEAPON_NAVIGATOR =
            ITEMS.register("navigator", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.PISTOL, 12, 1, 2.0f));

    // ===== 施术单元（16把） =====
    public static final DeferredItem<Item> WEAPON_O_B_J_ARCANE_KNOWLEDGE =
            ITEMS.register("o_b_j_arcane_knowledge", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_WORK_EROSION_TRACE =
            ITEMS.register("work_erosion_trace", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_MISSION_ACCOMPLISHED =
            ITEMS.register("mission_accomplished", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_AUTO_NOVA =
            ITEMS.register("auto_nova", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_JIMINY12 =
            ITEMS.register("jiminy12", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_LONELY_BOAT =
            ITEMS.register("lonely_boat", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_PREACH_FREEDOM =
            ITEMS.register("preach_freedom", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_ELEGY =
            ITEMS.register("elegy", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_OCEAN_STAR_DREAM =
            ITEMS.register("ocean_star_dream", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_BLAST_UNIT =
            ITEMS.register("blast_unit", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_FLUORESCENT_THUNDER_FEATHER =
            ITEMS.register("fluorescent_thunder_feather", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_MO_NAIHE =
            ITEMS.register("mo_naihe", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_LOST_WILDERNESS =
            ITEMS.register("lost_wilderness", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_FORGET =
            ITEMS.register("forget", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_MIST_GLIMMER =
            ITEMS.register("mist_glimmer", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    public static final DeferredItem<Item> WEAPON_CHIVALRY =
            ITEMS.register("chivalry", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 14, 2, 1.0f));

    // 方庄宜专武 - 孤舟（支持GeckoLib 3D渲染）
    public static final DeferredItem<Item> WEAPON_LONELY_BOAT_FANGZHUANGYI =
            ITEMS.register("lonely_boat_fangzhuangyi", () -> new com.endshop.job.item.weapon.LonelyBoatFangzhuangyiItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.CASTING_UNIT, 16, 3, 1.2f));

    // ===== 长柄武器（9把） =====
    public static final DeferredItem<Item> WEAPON_J_E_T =
            ITEMS.register("j_e_t", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.POLEARM, 18, 2, 1.3f));

    public static final DeferredItem<Item> WEAPON_O_B_J_PEAK =
            ITEMS.register("o_b_j_peak", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.POLEARM, 18, 2, 1.3f));

    public static final DeferredItem<Item> WEAPON_CENTRIPETAL_PULL =
            ITEMS.register("centripetal_pull", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.POLEARM, 18, 2, 1.3f));

    public static final DeferredItem<Item> WEAPON_ANGEL_SLAYER =
            ITEMS.register("angel_slayer", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.POLEARM, 18, 2, 1.3f));

    public static final DeferredItem<Item> WEAPON_OPERO77 =
            ITEMS.register("opero77", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.POLEARM, 18, 2, 1.3f));

    public static final DeferredItem<Item> WEAPON_PATHFINDER_BEACON =
            ITEMS.register("pathfinder_beacon", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.POLEARM, 18, 2, 1.3f));

    public static final DeferredItem<Item> WEAPON_CHIMERA_JUSTICE =
            ITEMS.register("chimera_justice", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.POLEARM, 18, 2, 1.3f));

    public static final DeferredItem<Item> WEAPON_MOUNTAIN_BEARER =
            ITEMS.register("mountain_bearer", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.POLEARM, 18, 2, 1.3f));

    public static final DeferredItem<Item> WEAPON_VALIANT =
            ITEMS.register("valiant", () -> new com.endshop.job.item.weapon.WeaponItem(
                new net.minecraft.world.item.Item.Properties(), 
                com.endshop.job.item.weapon.WeaponType.POLEARM, 18, 2, 1.3f));

    // 创造标签
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENDSHOP_TAB = 
            CREATIVE_MODE_TABS.register("endshop_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MODID))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> JOB_BOOK.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // 基本物品
                        output.accept(JOB_BOOK.get());
                        output.accept(ZHUANG_FANGYI_ART_ZONG_XIANG_YI.get());
                        output.accept(ZHUANG_FANGYI_ART_YU_TING_DE_SHUN_JIAN.get());
                        output.accept(ZHUANG_FANGYI_ART_DA_JU_YI_DING.get());
                        output.accept(JET_DRONE_ITEM.get());
                        output.accept(EROSION_BUCKET.get());
                        output.accept(EROSION_CORE_ITEM.get());
                        
                        // 珍贵物品（23个）
                        output.accept(GACHA_TICKET_STANDARD.get());
                        output.accept(GACHA_TICKET_SPECIAL.get());
                        output.accept(CHIP_BASIC.get());
                        output.accept(CHIP_ADVANCED.get());
                        output.accept(CHIP_SUPREME.get());
                        output.accept(ELITE_MATERIAL_1.get());
                        output.accept(ELITE_MATERIAL_2.get());
                        output.accept(ELITE_MATERIAL_3.get());
                        output.accept(EXP_BOOK_LOW.get());
                        output.accept(EXP_BOOK_MID.get());
                        output.accept(EXP_BOOK_HIGH.get());
                        output.accept(TACTICAL_MANUAL_LOW.get());
                        output.accept(TACTICAL_MANUAL_MID.get());
                        output.accept(TACTICAL_MANUAL_HIGH.get());
                        output.accept(SKILL_SUMMARY_1.get());
                        output.accept(SKILL_SUMMARY_2.get());
                        output.accept(SKILL_SUMMARY_3.get());
                        output.accept(ORIROCK_CUBE.get());
                        output.accept(ORORIGINIUM_DERIVATIVE.get());
                        output.accept(POTENTIAL_SHARD.get());
                        output.accept(TRACE_SURVIVAL.get());
                        output.accept(VOUCHER_SPECIAL.get());
                        output.accept(VOUCHER_STAR.get());
                        
                        // 单手剑（19把）
                        output.accept(WEAPON_O_B_J_LIGHT_EDGE.get());
                        output.accept(WEAPON_NO_RETURN.get());
                        output.accept(WEAPON_LOOK_UP.get());
                        output.accept(WEAPON_GLORY_MEMORY.get());
                        output.accept(WEAPON_TWELVE_QUESTIONS.get());
                        output.accept(WEAPON_FORTRESS_FORGER.get());
                        output.accept(WEAPON_TAR11.get());
                        output.accept(WEAPON_GREAT_WISH.get());
                        output.accept(WEAPON_SOARING.get());
                        output.accept(WEAPON_FAMOUS_REPUTATION.get());
                        output.accept(WEAPON_SHARP_EDGE.get());
                        output.accept(WEAPON_WAVE.get());
                        output.accept(WEAPON_THERMAL_CUTTER.get());
                        output.accept(WEAPON_MOLTEN_FLAME.get());
                        output.accept(WEAPON_WOLF_CRIMSON.get());
                        output.accept(WEAPON_WHITE_NIGHT_STAR.get());
                        output.accept(WEAPON_SCALE_CHASER3_0.get());
                        output.accept(WEAPON_STEEL_ECHO.get());
                        output.accept(WEAPON_DARK_TORCH.get());
                        
                        // 双手剑（12把）
                        output.accept(WEAPON_O_B_J_HEAVY_LOAD.get());
                        output.accept(WEAPON_PARAGON.get());
                        output.accept(WEAPON_ANCIENT_CHANNEL.get());
                        output.accept(WEAPON_THUNDER_SPOT.get());
                        output.accept(WEAPON_INDUSTRY_ZERO_ONE.get());
                        output.accept(WEAPON_DRAGON_SEEKER.get());
                        output.accept(WEAPON_PAST_MASTERPIECE.get());
                        output.accept(WEAPON_QUENCHER.get());
                        output.accept(WEAPON_BROKEN_KING.get());
                        output.accept(WEAPON_FINAL_VOICE.get());
                        output.accept(WEAPON_HRAFEN.get());
                        output.accept(WEAPON_DARHOF7.get());
                        
                        // 手铳（12把）
                        output.accept(WEAPON_O_B_J_SWIFT_EXTREME.get());
                        output.accept(WEAPON_WORK_ALL_BEINGS.get());
                        output.accept(WEAPON_PEKO5.get());
                        output.accept(WEAPON_CANNIBALISM.get());
                        output.accept(WEAPON_HOWLING_GUARD.get());
                        output.accept(WEAPON_HOMESICK.get());
                        output.accept(WEAPON_WEDGE.get());
                        output.accept(WEAPON_RATIONAL_FAREWELL.get());
                        output.accept(WEAPON_ART_TYRANT.get());
                        output.accept(WEAPON_FALL_GRASS.get());
                        output.accept(WEAPON_LONG_ROAD.get());
                        output.accept(WEAPON_NAVIGATOR.get());
                        
                        // 施术单元（17把）
                        output.accept(WEAPON_O_B_J_ARCANE_KNOWLEDGE.get());
                        output.accept(WEAPON_WORK_EROSION_TRACE.get());
                        output.accept(WEAPON_MISSION_ACCOMPLISHED.get());
                        output.accept(WEAPON_AUTO_NOVA.get());
                        output.accept(WEAPON_JIMINY12.get());
                        output.accept(WEAPON_LONELY_BOAT.get());
                        output.accept(WEAPON_LONELY_BOAT_FANGZHUANGYI.get());  // 方庄宜专武 - 孤舟
                        output.accept(WEAPON_PREACH_FREEDOM.get());
                        output.accept(WEAPON_ELEGY.get());
                        output.accept(WEAPON_OCEAN_STAR_DREAM.get());
                        output.accept(WEAPON_BLAST_UNIT.get());
                        output.accept(WEAPON_FLUORESCENT_THUNDER_FEATHER.get());
                        output.accept(WEAPON_MO_NAIHE.get());
                        output.accept(WEAPON_LOST_WILDERNESS.get());
                        output.accept(WEAPON_FORGET.get());
                        output.accept(WEAPON_MIST_GLIMMER.get());
                        output.accept(WEAPON_CHIVALRY.get());
                        
                        // 长柄武器（9把）
                        output.accept(WEAPON_J_E_T.get());
                        output.accept(WEAPON_O_B_J_PEAK.get());
                        output.accept(WEAPON_CENTRIPETAL_PULL.get());
                        output.accept(WEAPON_ANGEL_SLAYER.get());
                        output.accept(WEAPON_OPERO77.get());
                        output.accept(WEAPON_PATHFINDER_BEACON.get());
                        output.accept(WEAPON_CHIMERA_JUSTICE.get());
                        output.accept(WEAPON_MOUNTAIN_BEARER.get());
                        output.accept(WEAPON_VALIANT.get());
                    })
                    .build());

    public EndshopJob(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    
        // 注册配置
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, com.endshop.job.config.VillagerTradeConfig.SPEC);
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.SERVER, com.endshop.job.config.DailySignInConfig.SPEC);
        EndshopJob.LOGGER.info("[终末地职业] 配置文件已加载");
    
        // 注册实体类型
        EndshopEntityTypes.register(modEventBus);
    
        // 注册自定义效果
        com.endshop.job.effect.ModEffects.register(modEventBus);
        
        // 注册自定义属性（法术抗性等）
        com.endshop.job.attribute.ModAttributes.register(modEventBus);
        
        // 注册流体类型（NeoForge专属，必须在Fluid之前注册）
        com.endshop.job.fluid.ModFluidTypes.register(modEventBus);
        
        // 注册流体
        com.endshop.job.fluid.ModFluids.register(modEventBus);
        
        // 注册方块
        com.endshop.job.block.ModBlocks.register(modEventBus);
    
        // 注册数据附件类型
        JobDataAttachment.register(modEventBus);
        com.endshop.job.data.AttributeDataAttachment.register(modEventBus);
        com.endshop.job.data.ExperienceDataAttachment.register(modEventBus);
            
        // 注册技能数据附件
        SkillDataAttachment.register(modEventBus);
            
        // 注册任务数据附件
        com.endshop.job.task.TaskDataAttachment.register(modEventBus);
            
        // 注册天赋数据附件
        com.endshop.job.talent.extended.TalentAttachments.ATTACHMENTS.register(modEventBus);
            
        // 注册魔力数据附件
        com.endshop.job.talent.extended.ManaDataAttachment.register(modEventBus);
            
        // 注册体力数据附件
        com.endshop.job.combat.stamina.StaminaDataAttachment.register(modEventBus);
            
        // 注册技能能量数据附件
        com.endshop.job.combat.energy.SkillEnergyDataAttachment.register(modEventBus);
        
        // 注册敌人数据附件
        com.endshop.job.data.EnemyDataAttachment.register(modEventBus);
        
        // 注册饥饿数据附件
        com.endshop.job.data.HungerDataAttachment.register(modEventBus);
        
        // 注册装备数据附件
        com.endshop.job.data.EquipmentDataAttachment.register(modEventBus);
        
        // 注册村民数据附件
        com.endshop.job.data.VillagerDataAttachment.register(modEventBus);
        
        // 注册敌人击杀成就附件
        com.endshop.job.achievement.EnemyKillAchievement.register(modEventBus);
        
        // 注册敌人能力数据附件
        com.endshop.job.data.EnemyAbilityData.register(modEventBus);
        
        // 注册通行证数据附件
        com.endshop.job.battlepass.BattlePassAttachment.register(modEventBus);
        
        // 注册每日签到数据附件
        com.endshop.job.dailyreward.DailySignInAttachment.register(modEventBus);
        
        // 注册理智值数据附件
        com.endshop.job.sanity.SanityDataAttachment.register(modEventBus);
        
        // 注册天赋树菜单
        com.endshop.job.client.gui.talent.TalentTreeMenu.register(modEventBus);
            
        // 初始化并注册所有技能
        SkillInitializer.init();
        
        // 初始化干员档案
        OperatorProfileManager.initialize();
        
        // 初始化通行证系统
        com.endshop.job.battlepass.BattlePassManager.initializeSeasons();
        
        // 注册游戏事件（Tab 名显示、指令等）到 NeoForge 总线
        NeoForge.EVENT_BUS.register(new GameEvents());
        
        // 监听配置加载完成事件，然后初始化每日签到系统和通行证系统
        modEventBus.addListener(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent.class, event -> {
            event.enqueueWork(() -> {
                com.endshop.job.dailyreward.DailySignInManager.initializeRewards();
                EndshopJob.LOGGER.info("[终末地职业] 每日签到系统已初始化");
                
                com.endshop.job.battlepass.BattlePassManager.initializeSeasons();
                EndshopJob.LOGGER.info("[终末地职业] 通行证系统已初始化");
            });
        });
    }
    
    /**
     * 游戏事件处理类
     */
    private static class GameEvents {
        @SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            com.endshop.job.command.SetJobCommand.register(event.getDispatcher());
            com.endshop.job.command.AdminTaskCommand.register(event.getDispatcher());
            com.endshop.job.command.ErosionDebugCommand.register(event.getDispatcher());
            com.endshop.job.command.TalentCommand.register(event.getDispatcher());
            com.endshop.job.command.DailySignInCommand.register(event.getDispatcher());
            com.endshop.job.command.SanityCommand.register(event.getDispatcher());
            com.endshop.job.command.MaterialCalculatorCommand.register(event.getDispatcher());
            com.endshop.job.battlepass.BattlePassCommand.register(event.getDispatcher());
            com.endshop.job.command.AdminCommand.register(event.getDispatcher());
        }
    }


}