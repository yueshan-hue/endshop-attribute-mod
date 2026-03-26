"""
生成16个职业的像素风格书本图标 (64x64)
书本背景：棕色皮革封面，每个职业有专属像素图案
"""
from PIL import Image, ImageDraw, ImageFont
import os

OUT_DIR = r"E:/终末地商店/属性添加/src/main/resources/assets/endshop_job/textures/gui/jobs"
os.makedirs(OUT_DIR, exist_ok=True)

# ── 调色板 ─────────────────────────────────────────────────────────
BOOK_BG      = (240, 225, 185, 255)   # 米黄页面
BOOK_BORDER  = (101,  57,  27, 255)   # 深棕封面
BOOK_SPINE   = ( 75,  38,  15, 255)   # 书脊暗色
CORNER_GOLD  = (200, 160,  40, 255)   # 金色角扣
TRANSPARENT  = (0, 0, 0, 0)

def make_book_base(size=64):
    """画书本底板"""
    img = Image.new("RGBA", (size, size), TRANSPARENT)
    d = ImageDraw.Draw(img)
    s = size
    border = 4

    # 主体
    d.rounded_rectangle([border, border, s-border, s-border],
                         radius=6, fill=BOOK_BG, outline=BOOK_BORDER, width=3)
    # 左书脊
    d.rectangle([border, border, border+5, s-border], fill=BOOK_SPINE)
    d.rectangle([border+5, border+1, border+7, s-border-1], fill=BOOK_BORDER)
    # 四角金扣
    cs = 7
    for cx, cy in [(border, border), (s-border-cs, border),
                   (border, s-border-cs), (s-border-cs, s-border-cs)]:
        d.rectangle([cx, cy, cx+cs, cy+cs], fill=CORNER_GOLD)
        d.rectangle([cx+2, cy+2, cx+cs-2, cy+cs-2], fill=BOOK_BG)
    return img

def paste_icon(book, icon_pixels, size=64, icon_area=(14, 8, 58, 44)):
    """把图标像素阵列画到书上"""
    d = ImageDraw.Draw(book)
    x0, y0, x1, y1 = icon_area
    pw = (x1 - x0) / len(icon_pixels[0])
    ph = (y1 - y0) / len(icon_pixels)
    for iy, row in enumerate(icon_pixels):
        for ix, col in enumerate(row):
            if col is None:
                continue
            rx = int(x0 + ix * pw)
            ry = int(y0 + iy * ph)
            rw = max(1, int(pw))
            rh = max(1, int(ph))
            d.rectangle([rx, ry, rx+rw, ry+rh], fill=col)

def add_label(book, text, size=64):
    """在书底部居中写职业名"""
    d = ImageDraw.Draw(book)
    # 尝试使用系统中文字体
    font = None
    font_paths = [
        r"C:\Windows\Fonts\msyh.ttc",
        r"C:\Windows\Fonts\simsun.ttc",
        r"C:\Windows\Fonts\simhei.ttf",
    ]
    for fp in font_paths:
        if os.path.exists(fp):
            try:
                from PIL import ImageFont
                font = ImageFont.truetype(fp, 13)
                break
            except:
                pass
    if font is None:
        font = ImageFont.load_default()

    # 文字位置
    bbox = d.textbbox((0, 0), text, font=font)
    tw = bbox[2] - bbox[0]
    tx = (size - tw) // 2
    ty = size - 18
    # 阴影
    d.text((tx+1, ty+1), text, font=font, fill=(80, 50, 20, 200))
    d.text((tx, ty), text, font=font, fill=(60, 30, 10, 255))

# ── 各职业像素图案 ──────────────────────────────────────────────────
# 颜色简写
G  = (150,150,150,255)  # 灰（铁/石）
DG = ( 80, 80, 80,255)  # 深灰
LG = (200,200,200,255)  # 浅灰
BR = (139, 90, 43,255)  # 棕色
GO = (218,165, 32,255)  # 金色
R  = (200, 50, 50,255)  # 红
BL = ( 50,100,200,255)  # 蓝
GR = ( 60,160, 60,255)  # 绿
WH = (240,240,240,255)  # 白
BK = ( 30, 30, 30,255)  # 黑
OR = (220,120, 30,255)  # 橙
YE = (240,200,  0,255)  # 黄
PU = (140, 60,180,255)  # 紫

N  = None  # 透明

# ── 剑（战士） ──────────────────────────────────────────────────────
SWORD = [
    [N, N, N, N, N, N, N, LG],
    [N, N, N, N, N, N, LG, G ],
    [N, N, N, N, N, LG, G , N],
    [N, N, N, N, LG, G , N , N],
    [N, N, N, LG, G , N , N , N],
    [N, N, LG, G , N , N , N , N],
    [WH, G , N , N , N , N , N , N],
    [BR, BR, N , N , N , N , N , N],
    [N, BR, BR, N , N , N , N , N],
]

# ── 魔杖（法师） ───────────────────────────────────────────────────
WAND = [
    [N , N , N , N , N , N , BL, BL],
    [N , N , N , N , N , BL, WH, BL],
    [N , N , N , N , N , BL, BL, N ],
    [N , N , N , N , BL, N , N , N ],
    [N , N , N , BL, N , N , N , N ],
    [N , N , BR, N , N , N , N , N ],
    [N , BR, N , N , N , N , N , N ],
    [BR, N , N , N , N , N , N , N ],
    [N , N , N , N , N , N , N , N ],
]

# ── 弓（弓手） ────────────────────────────────────────────────────
BOW = [
    [N , BR, N , N , N , N , N , N ],
    [BR, N , G , N , N , N , N , N ],
    [BR, N , N , G , N , N , N , N ],
    [BR, N , N , N , G , N , N , N ],
    [BR, N , G , N , N , G , N , N ],
    [BR, N , N , G , N , N , G , N ],
    [BR, N , N , N , G , N , N , G ],
    [BR, N , G , N , N , N , N , N ],
    [N , BR, N , N , N , N , N , N ],
]

# ── 匕首（盗贼） ──────────────────────────────────────────────────
DAGGER = [
    [N , N , N , N , N , N , LG, LG],
    [N , N , N , N , N , LG, G , N ],
    [N , N , N , N , LG, G , N , N ],
    [N , N , N , G , N , N , N , N ],
    [N , N , G , BK, N , N , N , N ],
    [N , BR, G , N , N , N , N , N ],
    [BR, OR, BR, N , N , N , N , N ],
    [N , BR, N , N , N , N , N , N ],
    [N , N , N , N , N , N , N , N ],
]

# ── 十字架（牧师） ────────────────────────────────────────────────
CROSS = [
    [N , N , WH, WH, WH, N , N , N ],
    [N , N , WH, WH, WH, N , N , N ],
    [WH, WH, WH, WH, WH, WH, WH, N ],
    [WH, WH, WH, WH, WH, WH, WH, N ],
    [N , N , WH, WH, WH, N , N , N ],
    [N , N , WH, WH, WH, N , N , N ],
    [N , N , WH, WH, WH, N , N , N ],
    [N , N , WH, WH, WH, N , N , N ],
    [N , N , N , N , N , N , N , N ],
]

# ── 金剑（剑客） ──────────────────────────────────────────────────
GOLD_SWORD = [
    [N , N , N , N , N , N , YE, YE],
    [N , N , N , N , N , YE, GO, N ],
    [N , N , N , N , YE, GO, N , N ],
    [N , N , N , YE, GO, N , N , N ],
    [N , N , YE, GO, N , N , N , N ],
    [N , YE, GO, N , N , N , N , N ],
    [GO, OR, N , N , N , N , N , N ],
    [BR, BR, N , N , N , N , N , N ],
    [N , BR, BR, N , N , N , N , N ],
]

# ── 金球（术士） ──────────────────────────────────────────────────
ORB = [
    [N , N , YE, GO, GO, YE, N , N ],
    [N , GO, GO, OR, OR, GO, GO, N ],
    [YE, GO, OR, YE, OR, OR, GO, YE],
    [GO, OR, YE, OR, OR, OR, OR, GO],
    [GO, OR, OR, OR, OR, OR, OR, GO],
    [GO, GO, OR, OR, OR, OR, GO, GO],
    [N , GO, GO, OR, OR, GO, GO, N ],
    [N , N , YE, GO, GO, YE, N , N ],
    [N , N , N , N , N , N , N , N ],
]

# ── 长弓（游侠） ──────────────────────────────────────────────────
LONGBOW = [
    [N , N , BR, N , N , N , N , N ],
    [N , BR, N , LG, N , N , N , N ],
    [BR, N , N , N , LG, N , N , N ],
    [BR, N , N , N , N , LG, N , N ],
    [BR, N , N , N , LG, N , N , N ],
    [BR, N , N , LG, N , N , N , N ],
    [BR, N , LG, N , N , N , N , N ],
    [N , BR, N , N , N , N , N , N ],
    [N , N , BR, N , N , N , N , N ],
]

# ── 盾牌（刺客/圣骑士） ──────────────────────────────────────────
SHIELD = [
    [N , G , G , G , G , G , N , N ],
    [G , DG, DG, DG, DG, DG, G , N ],
    [G , DG, GO, GO, GO, DG, G , N ],
    [G , DG, GO, WH, GO, DG, G , N ],
    [G , DG, GO, GO, GO, DG, G , N ],
    [G , DG, DG, DG, DG, DG, G , N ],
    [N , G , G , G , G , G , N , N ],
    [N , N , G , G , G , N , N , N ],
    [N , N , N , G , N , N , N , N ],
]

# ── 镐（矿工） ────────────────────────────────────────────────────
PICKAXE = [
    [N , N , N , N , N , G , G , G ],
    [N , N , N , N , G , LG, G , N ],
    [N , N , N , G , LG, G , N , N ],
    [BR, N , G , LG, G , N , N , N ],
    [N , BR, N , G , N , N , N , N ],
    [N , N , BR, N , N , N , N , N ],
    [N , N , N , BR, N , N , N , N ],
    [N , N , N , N , BR, N , N , N ],
    [N , N , N , N , N , N , N , N ],
]

# ── 斧头（樵夫） ─────────────────────────────────────────────────
AXE = [
    [N , N , N , N , G , G , N , N ],
    [N , N , N , G , DG, G , G , N ],
    [N , N , BR, G , G , DG, G , N ],
    [N , BR, N , N , G , G , N , N ],
    [BR, N , N , N , N , N , N , N ],
    [N , BR, N , N , N , N , N , N ],
    [N , N , BR, N , N , N , N , N ],
    [N , N , N , BR, N , N , N , N ],
    [N , N , N , N , N , N , N , N ],
]

# ── 弩（猎人） ───────────────────────────────────────────────────
CROSSBOW = [
    [N , N , N , N , G , N , N , N ],
    [N , BR, BR, G , N , G , N , N ],
    [BR, N , N , G , N , N , G , N ],
    [G , G , G , G , G , G , G , N ],
    [BR, N , N , G , N , N , G , N ],
    [N , BR, BR, G , N , G , N , N ],
    [N , N , N , N , G , N , N , N ],
    [N , N , N , N , N , N , N , N ],
    [N , N , N , N , N , N , N , N ],
]

# ── 锄头（农夫） ─────────────────────────────────────────────────
HOE = [
    [N , N , N , N , G , G , G , N ],
    [N , N , N , G , DG, DG, G , N ],
    [N , N , N , G , G , G , N , N ],
    [N , N , BR, N , N , N , N , N ],
    [N , BR, N , N , N , N , N , N ],
    [BR, N , N , N , N , N , N , N ],
    [N , BR, N , N , N , N , N , N ],
    [N , N , BR, N , N , N , N , N ],
    [N , N , N , N , N , N , N , N ],
]

# ── 金币袋（商人） ───────────────────────────────────────────────
COIN_BAG = [
    [N , N , GO, YE, GO, N , N , N ],
    [N , GO, YE, YE, YE, GO, N , N ],
    [N , GO, GO, YE, GO, GO, N , N ],
    [GO, OR, GO, YE, GO, OR, GO, N ],
    [GO, OR, OR, OR, OR, OR, GO, N ],
    [GO, OR, OR, YE, OR, OR, GO, N ],
    [N , GO, OR, OR, OR, GO, N , N ],
    [N , N , GO, GO, GO, N , N , N ],
    [N , N , N , N , N , N , N , N ],
]

# ── 职业列表 ──────────────────────────────────────────────────────
JOBS = [
    ("warrior",   "战士", SWORD),
    ("mage",      "法师", WAND),
    ("archer",    "弓手", BOW),
    ("rogue",     "盗贼", DAGGER),
    ("priest",    "牧师", CROSS),
    ("swordsman", "剑客", GOLD_SWORD),
    ("warlock",   "术士", ORB),
    ("ranger",    "游侠", LONGBOW),
    ("assassin",  "刺客", DAGGER),
    ("paladin",   "圣骑", SHIELD),
    ("miner",     "矿工", PICKAXE),
    ("lumberjack","樵夫", AXE),
    ("hunter",    "猎人", CROSSBOW),
    ("farmer",    "农夫", HOE),
    ("merchant",  "商人", COIN_BAG),
]

for fname, label, pixels in JOBS:
    book = make_book_base(64)
    paste_icon(book, pixels, 64, icon_area=(14, 6, 56, 40))
    add_label(book, label, 64)
    path = os.path.join(OUT_DIR, f"{fname}.png")
    book.save(path)
    print(f"  生成: {fname}.png ({label})")

print(f"\n全部 {len(JOBS)} 个图标生成完毕 → {OUT_DIR}")
