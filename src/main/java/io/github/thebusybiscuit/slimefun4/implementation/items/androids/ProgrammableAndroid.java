package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import city.norain.slimefun4.api.menu.UniversalMenu;
import city.norain.slimefun4.api.menu.UniversalMenuPreset;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.ASlimefunDataContainer;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.attributes.UniversalBlock;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.bakedlibs.dough.chat.ChatInput;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.bakedlibs.dough.skins.PlayerHead;
import io.github.bakedlibs.dough.skins.PlayerSkin;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.rotations.NotDiagonallyRotatable;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.menu.AndroidShareMenu;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;

import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.AdvancedMenuClickHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.qscbm.slimefun4.utils.QsConstants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ProgrammableAndroid extends SlimefunItem
        implements InventoryBlock, RecipeDisplayItem, NotDiagonallyRotatable, UniversalBlock {
    private static final List<BlockFace> POSSIBLE_ROTATIONS =
            Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    private static final int[] BORDER = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 24, 26, 27, 33, 35, 36, 42, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53
    };
    private static final int[] OUTPUT_BORDER = {10, 11, 12, 13, 14, 19, 23, 28, 32, 37, 38, 39, 40, 41};
    private static final String DEFAULT_SCRIPT = "START-TURN_LEFT-REPEAT";
    private static final int MAX_SCRIPT_LENGTH = 54;

    protected final List<MachineFuel> fuelTypes = new ArrayList<>();
    protected static String texture;
    @Getter
    private final int tier;

    public ProgrammableAndroid(
            ItemGroup itemGroup, int tier, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        this.tier = tier;
        texture = item.getSkullTexture().orElse(null);
        registerDefaultFuelTypes();

        new UniversalMenuPreset(getId(), "可编程式机器人") {
            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                SlimefunUniversalBlockData uniData = StorageCacheUtils.getUniversalBlock(b);

                UUID owner = UUID.fromString(uniData.getData("owner"));

                boolean isOwner = p.getUniqueId().equals(owner) || p.hasPermission("slimefun.android.bypass");

                if (isOwner || AndroidShareMenu.isTrustedUser(b, p.getUniqueId())) {
                    return true;
                } else {
                    Slimefun.getLocalization().sendMessage(p, "inventory.no-access", true);
                    return false;
                }
            }

            @Override
            public void newInstance(UniversalMenu menu, Block b) {
                SlimefunUniversalBlockData uniData = StorageCacheUtils.getUniversalBlock(menu.getUuid());

                menu.replaceExistingItem(
                        15, new CustomItemStack(HeadTexture.SCRIPT_START.getAsItemStack(), "&a启动/继续运行"));
                menu.addMenuClickHandler(15, (p, slot, item, action) -> {
                    Slimefun.getLocalization().sendMessage(p, "android.started", true);
                    uniData.setData("paused", "false");
                    p.closeInventory();
                    return false;
                });

                menu.replaceExistingItem(17, new CustomItemStack(HeadTexture.SCRIPT_PAUSE.getAsItemStack(), "§4暂停运行"));
                menu.addMenuClickHandler(17, (p, slot, item, action) -> {
                    uniData.setData("paused", "true");
                    Slimefun.getLocalization().sendMessage(p, "android.stopped", true);
                    return false;
                });

                menu.replaceExistingItem(
                        16,
                        new CustomItemStack(
                                HeadTexture.ENERGY_REGULATOR.getAsItemStack(), "&b内存核心", "", "§8\u21E8 &7单击打开脚本编辑器"));
                menu.addMenuClickHandler(16, (p, slot, item, action) -> {
                    uniData.setData("paused", "true");
                    Slimefun.getLocalization().sendMessage(p, "android.stopped", true);
                    openScriptEditor(p, uniData);
                    return false;
                });

                menu.replaceExistingItem(
                        25,
                        new CustomItemStack(
                                HeadTexture.MOTOR.getAsItemStack(),
                                Slimefun.getLocalization().getMessage("android.access-manager.title"),
                                "",
                                Slimefun.getLocalization().getMessage("android.access-manager.subtitle")));
                menu.addMenuClickHandler(25, (p, slot, item, action) -> {
                    uniData.setData("paused", "true");
                    Slimefun.getLocalization().sendMessage(p, "android.stopped", true);
                    AndroidShareMenu.openShareMenu(p, b);
                    return false;
                });
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return QsConstants.EMPTY_INTS;
            }
        };

        addItemHandler(onPlace(), onBreak());
    }

    private static BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                Player p = e.getPlayer();
                Block b = e.getBlock();

                SlimefunUniversalBlockData universalData = StorageCacheUtils.getUniversalBlock(b);

                universalData.setData("owner", p.getUniqueId().toString());
                universalData.setData("script", DEFAULT_SCRIPT);
                universalData.setData("index", String.valueOf(0));
                universalData.setData("fuel", String.valueOf(0));
                universalData.setData(
                        "rotation", p.getFacing().getOppositeFace().toString());
                universalData.setData("paused", String.valueOf(true));

                b.setBlockData(Material.PLAYER_HEAD.createBlockData(data -> {
                    if (data instanceof Rotatable rotatable) {
                        rotatable.setRotation(p.getFacing());
                    }
                }));

                PlayerHead.setSkin(b, PlayerSkin.fromBase64(texture), true);
            }
        };
    }

    private BlockBreakHandler onBreak() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
                Block b = e.getBlock();

                SlimefunUniversalBlockData uniData = StorageCacheUtils.getUniversalBlock(b);
                if (!e.getPlayer().hasPermission("slimefun.android.bypass")
                    && !e.getPlayer().getUniqueId().toString().equals(uniData.getData("owner"))) {
                    // The Player is not allowed to break this android
                    e.setCancelled(true);
                    return;
                }

                if (!e.getPlayer().hasPermission("slimefun.android.bypass")
                        && !e.getPlayer().getUniqueId().toString().equals(uniData.getData("owner"))) {
                    // The Player is not allowed to break this android
                    e.setCancelled(true);
                    return;
                }

                UniversalMenu menu = uniData.getMenu();
                if (menu != null) {
                    menu.dropItems(b.getLocation(), 43);
                    menu.dropItems(b.getLocation(), getOutputSlots());
                }
            }
        };
    }

    /**
     * This returns the {@link AndroidType} that is associated with this {@link ProgrammableAndroid}.
     *
     * @return The type of this {@link ProgrammableAndroid}
     */
    public AndroidType getAndroidType() {
        return AndroidType.NONE;
    }

    /**
     * This returns the {@link AndroidFuelSource} for this {@link ProgrammableAndroid}.
     * It determines what kind of fuel is required to run it.
     *
     * @return The required type of fuel
     */
    public AndroidFuelSource getFuelSource() {
        return switch (tier) {
            case 1 -> AndroidFuelSource.SOLID;
            case 2 -> AndroidFuelSource.LIQUID;
            case 3 -> AndroidFuelSource.NUCLEAR;
            default -> throw new IllegalStateException(
                    "Cannot convert the following Android tier to a fuel type: " + tier);
        };
    }

    @Override
    public void preRegister() {
        super.preRegister();

        addItemHandler(new BlockTicker(true) {
            @Override
            public void tick(Block b, SlimefunItem item, SlimefunUniversalData data) {
                if (b != null && data != null) {
                    ProgrammableAndroid.this.tick(b, data);
                }
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }
        });
    }

    public void openScript(Player p, SlimefunUniversalBlockData uniData, String sourceCode) {
        ChestMenu menu =
                new ChestMenu(ChatColor.DARK_AQUA + Slimefun.getLocalization().getMessage(p, "android.scripts.editor"));
        menu.setEmptySlotsClickable(false);

        menu.addItem(
                0,
                new CustomItemStack(
                        Instruction.START.getItem(),
                        Slimefun.getLocalization().getMessage(p, "android.scripts.instructions.START"),
                        "",
                        "&7\u21E8 &e左键 &7返回机器人的控制面板"));
        menu.addMenuClickHandler(0, (pl, slot, item, action) -> {
            UniversalMenu inv = uniData.getMenu();
            // Fixes #2937
            if (inv != null) {
                inv.open(pl);
            } else {
                pl.closeInventory();
            }
            return false;
        });

        String[] script = sourceCode.split("-");

        for (int i = 1; i < script.length; i++) {
            int index = i;

            if (i == script.length - 1) {
                boolean hasFreeSlot = script.length < 54;

                if (hasFreeSlot) {
                    menu.addItem(i, new CustomItemStack(HeadTexture.SCRIPT_NEW.getAsItemStack(), "&7> 添加新命令"));
                    menu.addMenuClickHandler(i, (pl, slot, item, action) -> {
                        editInstruction(pl, uniData, script, index);
                        return false;
                    });
                }

                int slot = i + (hasFreeSlot ? 1 : 0);
                menu.addItem(
                        slot,
                        new CustomItemStack(
                                Instruction.REPEAT.getItem(),
                                Slimefun.getLocalization().getMessage(p, "android.scripts.instructions.REPEAT"),
                                "",
                                "&7\u21E8 &e左键 &7返回机器人的控制面板"));
                menu.addMenuClickHandler(slot, (pl, s, item, action) -> {
                    UniversalMenu inv = uniData.getMenu();
                    // Fixes #2937
                    if (inv != null) {
                        inv.open(pl);
                    } else {
                        pl.closeInventory();
                    }
                    return false;
                });
            } else {
                Instruction instruction = Instruction.getInstruction(script[i]);

                ItemStack stack = instruction.getItem();
                menu.addItem(
                        i,
                        new CustomItemStack(
                                stack,
                                Slimefun.getLocalization()
                                        .getMessage(
                                                p,
                                                "android.scripts.instructions."
                                                + Instruction.valueOf(script[i])
                                                        .name()),
                                "",
                                "&7\u21E8 &e左键 &7编辑",
                                "&7\u21E8 &e右键 &7删除",
                                "&7\u21E8 &eShift + 右键 &7复制"));
                menu.addMenuClickHandler(i, (pl, slot, item, action) -> {
                    if (action.isRightClicked() && action.isShiftClicked()) {
                        if (script.length == 54) {
                            return false;
                        }

                        String code = duplicateInstruction(script, index);
                        setScript(uniData, code);
                        openScript(pl, uniData, code);
                    } else if (action.isRightClicked()) {
                        String code = deleteInstruction(script, index);
                        setScript(uniData, code);
                        openScript(pl, uniData, code);
                    } else {
                        editInstruction(pl, uniData, script, index);
                    }

                    return false;
                });
            }
        }

        menu.open(p);
    }

    private static String addInstruction(String[] script, int index, Instruction instruction) {
        int i = 0;
        StringBuilder builder = new StringBuilder(Instruction.START.name() + '-');

        for (String current : script) {
            if (i > 0) {
                if (i == index) {
                    builder.append(instruction).append('-');
                } else if (i < script.length - 1) {
                    builder.append(current).append('-');
                }
            }
            i++;
        }

        builder.append(Instruction.REPEAT.name());
        return builder.toString();
    }

    private static String duplicateInstruction(String[] script, int index) {
        int i = 0;
        StringBuilder builder = new StringBuilder(Instruction.START + "-");

        for (String instruction : script) {
            if (i > 0) {
                if (i == index) {
                    builder.append(script[i]).append('-').append(script[i]).append('-');
                } else if (i < script.length - 1) {
                    builder.append(instruction).append('-');
                }
            }
            i++;
        }

        builder.append(Instruction.REPEAT.name());
        return builder.toString();
    }

    private static String deleteInstruction(String[] script, int index) {
        int i = 0;
        StringBuilder builder = new StringBuilder(Instruction.START.name() + '-');

        for (String instruction : script) {
            if (i != index && i > 0 && i < script.length - 1) {
                builder.append(instruction).append('-');
            }

            i++;
        }

        builder.append(Instruction.REPEAT.name());
        return builder.toString();
    }

    protected void openScriptDownloader(Player p, SlimefunUniversalBlockData uniData, int page) {
        ChestMenu menu = new ChestMenu("机器人脚本");

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(SoundEffect.PROGRAMMABLE_ANDROID_SCRIPT_DOWNLOAD_SOUND::playFor);

        List<Script> scripts = Script.getUploadedScripts(getAndroidType());
        int pages = (scripts.size() / 45) + 1;

        for (int i = 45; i < 54; i++) {
            menu.addItem(i, ChestMenuUtils.getBackground());
            menu.addMenuClickHandler(i, ChestMenuUtils.getEmptyClickHandler());
        }

        menu.addItem(46, ChestMenuUtils.getPreviousButton(p, page, pages));
        menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
            int next = page - 1;
            if (next < 1) {
                next = pages;
            }
            if (next != page) {
                openScriptDownloader(pl, uniData, next);
            }
            return false;
        });

        menu.addItem(
                48,
                new CustomItemStack(
                        HeadTexture.SCRIPT_UP.getAsItemStack(), "&e上传脚本", "", "&6单击 &7将你正在用的脚本", "&7上传到服务器"));
        menu.addMenuClickHandler(48, (pl, slot, item, action) -> {
            uploadScript(pl, uniData, page);
            return false;
        });

        menu.addItem(50, ChestMenuUtils.getNextButton(p, page, pages));
        menu.addMenuClickHandler(50, (pl, slot, item, action) -> {
            int next = page + 1;
            if (next > pages) {
                next = 1;
            }
            if (next != page) {
                openScriptDownloader(pl, uniData, next);
            }
            return false;
        });

        menu.addItem(53, new CustomItemStack(HeadTexture.SCRIPT_LEFT.getAsItemStack(), "&6> 返回", "", "&7返回机器人控制面板"));
        menu.addMenuClickHandler(53, (pl, slot, item, action) -> {
            openScriptEditor(pl, uniData);
            return false;
        });

        int index = 0;
        int categoryIndex = 45 * (page - 1);

        for (int i = 0; i < 45; i++) {
            int target = categoryIndex + i;

            if (target >= scripts.size()) {
                break;
            } else {
                Script script = scripts.get(target);
                menu.addItem(index, script.getAsItemStack(this, p), (player, slot, stack, action) -> {
                    try {
                        if (action.isShiftClicked()) {
                            if (script.isAuthor(player)) {
                                Slimefun.getLocalization().sendMessage(player, "android.scripts.rating.own", true);
                            } else if (script.canRate(player)) {
                                script.rate(player, !action.isRightClicked());
                                openScriptDownloader(player, uniData, page);
                            } else {
                                Slimefun.getLocalization().sendMessage(player, "android.scripts.rating.already", true);
                            }
                        } else if (!action.isRightClicked()) {
                            script.download();
                            setScript(uniData, script.getSourceCode());
                            openScriptEditor(player, uniData);
                        }
                    } catch (RuntimeException x) {
                        Slimefun.logger()
                                .log(
                                        Level.SEVERE,
                                        "An Exception was thrown when a User tried to download a Script!",
                                        x);
                    }

                    return false;
                });

                index++;
            }
        }

        menu.open(p);
    }

    private void uploadScript(Player p, SlimefunUniversalBlockData uniData, int page) {
        String code = getScript(uniData);
        int nextId = 1;

        for (Script script : Script.getUploadedScripts(getAndroidType())) {
            if (script.isAuthor(p)) {
                nextId++;
            }

            if (script.getSourceCode().equals(code)) {
                Slimefun.getLocalization().sendMessage(p, "android.scripts.already-uploaded", true);
                return;
            }
        }

        p.closeInventory();
        Slimefun.getLocalization().sendMessages(p, "android.scripts.enter-name");
        int id = nextId;

        ChatInput.waitForPlayer(Slimefun.instance(), p, msg -> {
            Script.upload(p, getAndroidType(), id, msg, code);
            Slimefun.getLocalization().sendMessages(p, "android.scripts.uploaded");
            openScriptDownloader(p, uniData, page);
        });
    }

    public void openScriptEditor(Player p, SlimefunUniversalBlockData uniData) {
        ChestMenu menu =
                new ChestMenu(ChatColor.DARK_AQUA + Slimefun.getLocalization().getMessage(p, "android.scripts.editor"));
        menu.setEmptySlotsClickable(false);

        menu.addItem(1, new CustomItemStack(HeadTexture.SCRIPT_FORWARD.getAsItemStack(), "&2> 编辑脚本", "", "&a修改你现有的脚本"));
        menu.addMenuClickHandler(1, (pl, slot, item, action) -> {
            String script = getScript(uniData);
            if (script.split("-").length <= MAX_SCRIPT_LENGTH) {
                openScript(pl, uniData, script);
            } else {
                pl.closeInventory();
                Slimefun.getLocalization().sendMessage(pl, "android.scripts.too-long");
            }
            return false;
        });

        menu.addItem(
                3,
                new CustomItemStack(
                        HeadTexture.SCRIPT_NEW.getAsItemStack(), "§4> 创建新脚本", "", "&c删除你正在使用的脚本", "&c并创建一个全新的空白脚本"));
        menu.addMenuClickHandler(3, (pl, slot, item, action) -> {
            openScript(pl, uniData, DEFAULT_SCRIPT);
            return false;
        });

        menu.addItem(
                5,
                new CustomItemStack(
                        HeadTexture.SCRIPT_DOWN.getAsItemStack(),
                        "&6> 下载脚本",
                        "",
                        "&e从服务器中下载其他玩家上传的脚本",
                        "&e可以即下即用, 或者修改之后再使用"));
        menu.addMenuClickHandler(5, (pl, slot, item, action) -> {
            openScriptDownloader(pl, uniData, 1);
            return false;
        });

        menu.addItem(8, new CustomItemStack(HeadTexture.SCRIPT_LEFT.getAsItemStack(), "&6> 返回", "", "&7返回机器人控制面板"));
        menu.addMenuClickHandler(8, (pl, slot, item, action) -> {
            UniversalMenu inv = uniData.getMenu();
            // Fixes #2937
            if (inv != null) {
                inv.open(pl);
            } else {
                pl.closeInventory();
            }
            return false;
        });

        menu.open(p);
    }

    protected List<Instruction> getValidScriptInstructions() {
        List<Instruction> list = new ArrayList<>();

        for (Instruction part : Instruction.valuesCache) {
            if (part == Instruction.START || part == Instruction.REPEAT) {
                continue;
            }

            if (getAndroidType().isType(part.getRequiredType())) {
                list.add(part);
            }
        }

        return list;
    }

    protected void editInstruction(Player p, SlimefunUniversalBlockData uniData, String[] script, int index) {
        ChestMenu menu =
                new ChestMenu(ChatColor.DARK_AQUA + Slimefun.getLocalization().getMessage(p, "android.scripts.editor"));
        ChestMenuUtils.drawBackground(menu, 0, 1, 2, 3, 4, 5, 6, 7, 8);

        menu.setEmptySlotsClickable(false);
        menu.addItem(
                9,
                new CustomItemStack(HeadTexture.SCRIPT_PAUSE.getAsItemStack(), "&f什么也不做"),
                (pl, slot, item, action) -> {
                    String code = deleteInstruction(script, index);
                    setScript(uniData, code);
                    openScript(p, uniData, code);
                    return false;
                });

        int i = 10;
        for (Instruction instruction : getValidScriptInstructions()) {
            menu.addItem(
                    i,
                    new CustomItemStack(
                            instruction.getItem(),
                            Slimefun.getLocalization()
                                    .getMessage(p, "android.scripts.instructions." + instruction.name())),
                    (pl, slot, item, action) -> {
                        String code = addInstruction(script, index, instruction);
                        setScript(uniData, code);
                        openScript(p, uniData, code);
                        return false;
                    });

            i++;
        }

        menu.open(p);
    }

    public static String getScript(SlimefunUniversalBlockData ubd) {
        String script = ubd.getData("script");
        return script != null ? script : DEFAULT_SCRIPT;
    }

    public static void setScript(SlimefunUniversalBlockData ubd, String script) {
        ubd.setData("script", script);
    }

    private void registerDefaultFuelTypes() {
        switch (getFuelSource()) {
            case SOLID -> {
                registerFuelType(new MachineFuel(80, new ItemStack(Material.COAL_BLOCK)));
                registerFuelType(new MachineFuel(45, new ItemStack(Material.BLAZE_ROD)));
                registerFuelType(new MachineFuel(70, new ItemStack(Material.DRIED_KELP_BLOCK)));

                // Coal, Charcoal & Bamboo
                registerFuelType(new MachineFuel(8, new ItemStack(Material.COAL)));
                registerFuelType(new MachineFuel(8, new ItemStack(Material.CHARCOAL)));
                registerFuelType(new MachineFuel(1, new ItemStack(Material.BAMBOO)));

                // Logs
                for (Material mat : Tag.LOGS.getValues()) {
                    registerFuelType(new MachineFuel(2, new ItemStack(mat)));
                }

                // Wooden Planks
                for (Material mat : Tag.PLANKS.getValues()) {
                    registerFuelType(new MachineFuel(1, new ItemStack(mat)));
                }
            }
            case LIQUID -> {
                registerFuelType(new MachineFuel(100, new ItemStack(Material.LAVA_BUCKET)));
                registerFuelType(new MachineFuel(200, SlimefunItems.OIL_BUCKET));
                registerFuelType(new MachineFuel(500, SlimefunItems.FUEL_BUCKET));
            }
            case NUCLEAR -> {
                registerFuelType(new MachineFuel(2500, SlimefunItems.URANIUM));
                registerFuelType(new MachineFuel(1200, SlimefunItems.NEPTUNIUM));
                registerFuelType(new MachineFuel(3000, SlimefunItems.BOOSTED_URANIUM));
            }
            default -> throw new IllegalStateException("Unhandled Fuel Source: " + getFuelSource());
        }
    }

    public void registerFuelType(MachineFuel fuel) {
        fuelTypes.add(fuel);
    }

    @Override
    public String getLabelLocalPath() {
        return "guide.tooltips.recipes.generator";
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> list = new ArrayList<>();

        for (MachineFuel fuel : fuelTypes) {
            ItemStack item = fuel.getInput().clone();
            ItemMeta im = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§8\u21E8 §7剩余 " + NumberUtils.getTimeLeft(fuel.getTicks() / 2));
            im.setLore(lore);
            item.setItemMeta(im);
            list.add(item);
        }

        return list;
    }

    @Override
    public int[] getInputSlots() {
        return QsConstants.EMPTY_INTS;
    }

    @Override
    public int[] getOutputSlots() {
        return new int[]{20, 21, 22, 29, 30, 31};
    }

    protected void tick(Block b, SlimefunUniversalData data) {
        if (b.getType() != Material.PLAYER_HEAD) {
            // The Android was destroyed or moved.
            return;
        }

        if ("false".equals(data.getData("paused"))) {
            UniversalMenu menu = data.getMenu();

            String fuelData = data.getData("fuel");
            float fuel = fuelData == null ? 0 : Float.parseFloat(fuelData);

            if (fuel < 0.001) {
                consumeFuel(b, menu);
            } else {
                String code = data.getData("script");
                String[] script = (code == null ? DEFAULT_SCRIPT : code).split("-");

                String indexData = data.getData("index");
                int index = (indexData == null ? 0 : Integer.parseInt(indexData)) + 1;

                if (index >= script.length) {
                    index = 0;
                }

                Instruction instruction = Instruction.getInstruction(script[index]);

                if ("false".equals(data.getData("paused"))) {
                    executeInstruction(instruction, b, menu, data, index);

                    data.setData("fuel", String.valueOf(fuel - 1));
                }
            }
        }
    }

    private void executeInstruction(
            Instruction instruction, Block b, UniversalMenu inv, SlimefunUniversalData data, int index) {
        if ("true".equals(data.getData("paused"))) {
            return;
        }

        if (getAndroidType().isType(instruction.getRequiredType())) {
            String rotationData = data.getData("rotation");
            BlockFace face = rotationData == null ? BlockFace.NORTH : BlockFace.valueOf(rotationData);

            switch (instruction) {
                case START, WAIT ->
                    // We are "waiting" here, so we only move a step forward
                        data.setData("index", String.valueOf(index));
                case REPEAT ->
                    // "repeat" just means, we reset our index
                        data.setData("index", String.valueOf(0));
                case CHOP_TREE -> {
                    // We only move to the next step if we finished chopping wood
                    if (chopTree(b, inv, face)) {
                        data.setData("index", String.valueOf(index));
                    }
                }
                default -> {
                    // We set the index here in advance to fix moving android issues
                    data.setData("index", String.valueOf(index));
                    instruction.execute(this, b, inv, face);
                }
            }
        }
    }

    public static void rotate(Block b, SlimefunUniversalBlockData uniData, BlockFace current, int mod) {
        int index = POSSIBLE_ROTATIONS.indexOf(current) + mod;

        if (index == POSSIBLE_ROTATIONS.size()) {
            index = 0;
        } else if (index < 0) {
            index = POSSIBLE_ROTATIONS.size() - 1;
        }

        BlockFace rotation = POSSIBLE_ROTATIONS.get(index);

        b.setBlockData(Material.PLAYER_HEAD.createBlockData(data -> {
            if (data instanceof Rotatable rotatable) {
                rotatable.setRotation(rotation.getOppositeFace());
            }
        }));

        uniData.setData("rotation", rotation.name());
    }

    protected void depositItems(UniversalMenu menu, Block facedBlock) {
        if (facedBlock.getType() == Material.DISPENSER
            && StorageCacheUtils.isBlock(facedBlock.getLocation(), "ANDROID_INTERFACE_ITEMS")) {
            BlockState state = facedBlock.getState(false);

            if (state instanceof Dispenser dispenser) {
                for (int slot : getOutputSlots()) {
                    ItemStack stack = menu.getItemInSlot(slot);

                    if (stack != null) {
                        Optional<ItemStack> optional = dispenser.getInventory().addItem(stack).values().stream()
                                .findFirst();

                        if (optional.isPresent()) {
                            menu.replaceExistingItem(slot, optional.get());
                        } else {
                            menu.replaceExistingItem(slot, null);
                        }
                    }
                }
            }
        }
    }

    public static void refuel(UniversalMenu menu, Block facedBlock) {
        if (facedBlock.getType() == Material.DISPENSER
            && StorageCacheUtils.isBlock(facedBlock.getLocation(), "ANDROID_INTERFACE_FUEL")) {
            BlockState state = facedBlock.getState(false);

            if (state instanceof Dispenser dispenser) {
                for (int slot = 0; slot < 9; slot++) {
                    ItemStack item = dispenser.getInventory().getItem(slot);

                    if (item != null) {
                        insertFuel(menu, dispenser.getInventory(), slot, menu.getItemInSlot(43), item);
                    }
                }
            }
        }
    }

    private static boolean insertFuel(
            UniversalMenu menu, Inventory dispenser, int slot, ItemStack currentFuel, ItemStack newFuel) {
        if (currentFuel == null) {
            menu.replaceExistingItem(43, newFuel);
            dispenser.setItem(slot, null);
            return true;
        } else if (SlimefunUtils.isItemSimilar(newFuel, currentFuel, true, false)) {
            int rest = newFuel.getType().getMaxStackSize() - currentFuel.getAmount();

            if (rest > 0) {
                int amount = Math.min(newFuel.getAmount(), rest);
                menu.replaceExistingItem(43, new CustomItemStack(newFuel, currentFuel.getAmount() + amount));
                ItemUtils.consumeItem(newFuel, amount, false);
            }

            return true;
        }

        return false;
    }

    private void consumeFuel(Block b, UniversalMenu menu) {
        ItemStack item = menu.getItemInSlot(43);

        if (item != null && item.getType() != Material.AIR) {
            for (MachineFuel fuel : fuelTypes) {
                if (fuel.test(item)) {
                    menu.consumeItem(43);

                    if (getFuelSource() == AndroidFuelSource.LIQUID) {
                        menu.pushItem(new ItemStack(Material.BUCKET), getOutputSlots());
                    }

                    int fuelLevel = fuel.getTicks();
                    StorageCacheUtils.setData(b.getLocation(), "fuel", String.valueOf(fuelLevel));
                    break;
                }
            }
        }
    }

    private void constructMenu(UniversalMenuPreset preset) {
        preset.drawBackground(BORDER);
        preset.drawBackground(ChestMenuUtils.getOutputSlotTexture(), OUTPUT_BORDER);

        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new AdvancedMenuClickHandler() {
                @Override
                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }

                @Override
                public boolean onClick(
                        InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                    return cursor == null || cursor.getType() == Material.AIR;
                }
            });
        }

        preset.addItem(34, getFuelSource().getItem(), ChestMenuUtils.getEmptyClickHandler());
    }

    public void addItems(Block b, ItemStack... items) {
        Optional<UUID> uuid = Slimefun.getBlockDataService().getUniversalDataUUID(b);

        UniversalMenu inv = StorageCacheUtils.getUniversalMenu(uuid.get(), b.getLocation());

        if (inv != null) {
            for (ItemStack item : items) {
                inv.pushItem(item, getOutputSlots());
            }
        }
    }

    protected void move(Block from, BlockFace face, Block to) {
        SlimefunUniversalBlockData uniData = StorageCacheUtils.getUniversalBlock(from);

        OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(uniData.getData("owner")));

        if (!Slimefun.getProtectionManager().hasPermission(owner, to.getLocation(), Interaction.PLACE_BLOCK)) {
            return;
        }
        if (to.getY() > to.getWorld().getMinHeight()
            && to.getY() < to.getWorld().getMaxHeight()
            && to.isEmpty()) {
            if (!to.getWorld().getWorldBorder().isInside(to.getLocation())) {
                return;
            }

            Slimefun.getTickerTask().disableTicker(from.getLocation());

            // Bro encountered a ghost 💀
            if (StorageCacheUtils.hasSlimefunBlock(to.getLocation())) {
                ASlimefunDataContainer data = StorageCacheUtils.getBlock(to.getLocation()) == null
                        ? StorageCacheUtils.getBlock(to.getLocation())
                        : StorageCacheUtils.getUniversalBlock(to);
                if (data != null && !data.isPendingRemove()) {
                    // Since it's a ghost, we just hunt it.
                    Slimefun.getDatabaseManager().getBlockDataController().removeBlock(to.getLocation());
                }
                return;
            }

            to.setBlockData(Material.PLAYER_HEAD.createBlockData(data -> {
                if (data instanceof Rotatable rotatable) {
                    rotatable.setRotation(face.getOppositeFace());
                }
            }));

            Slimefun.getBlockDataService()
                    .updateUniversalDataUUID(to, uniData.getUUID().toString());

            Slimefun.runSync(() -> {
                PlayerSkin skin = PlayerSkin.fromBase64(texture);
                Material type = to.getType();
                // Ensure that this Block is still a Player Head
                if (type == Material.PLAYER_HEAD || type == Material.PLAYER_WALL_HEAD) {
                    PlayerHead.setSkin(to, skin, true);
                }
            });

            from.setType(Material.AIR);
            uniData.setLastPresent(new BlockPosition(to.getLocation()));
            uniData.getMenu().update(to.getLocation());

            Slimefun.getTickerTask().enableTicker(to.getLocation(), uniData.getUUID());
        }
    }

    protected void attack(Block b, BlockFace face, Predicate<LivingEntity> predicate) {
        throw new UnsupportedOperationException("Non-butcher Android tried to butcher!");
    }

    protected void fish(Block b, UniversalMenu menu) {
        throw new UnsupportedOperationException("Non-fishing Android tried to fish!");
    }

    protected void dig(Block b, UniversalMenu menu, Block block) {
        throw new UnsupportedOperationException("Non-mining Android tried to mine!");
    }

    protected void moveAndDig(Block b, UniversalMenu menu, BlockFace face, Block block) {
        throw new UnsupportedOperationException("Non-mining Android tried to mine!");
    }

    protected boolean chopTree(Block b, UniversalMenu menu, BlockFace face) {
        throw new UnsupportedOperationException("Non-woodcutter Android tried to chop a Tree!");
    }

    protected void farm(Block b, UniversalMenu menu, Block block, boolean isAdvanced) {
        throw new UnsupportedOperationException("Non-farming Android tried to farm!");
    }
}
