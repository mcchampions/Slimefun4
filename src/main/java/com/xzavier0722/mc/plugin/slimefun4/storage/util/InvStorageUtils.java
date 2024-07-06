package com.xzavier0722.mc.plugin.slimefun4.storage.util;

import io.github.bakedlibs.dough.collections.Pair;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InvStorageUtils {
    private static final Pair<ItemStack, Integer> emptyPair = new Pair<>(null, 0);

    public static Set<Integer> getChangedSlots(List<Pair<ItemStack, Integer>> snapshot, ItemStack[] currContent) {
        var isEmptySnapshot = (snapshot == null || snapshot.isEmpty());
        if (isEmptySnapshot && currContent == null) {
            return Collections.emptySet();
        }

        var re = new HashSet<Integer>();
        if (isEmptySnapshot) {
            for (var i = 0; i < currContent.length; i++) {
                re.add(i);
            }
            return re;
        }

        if (currContent == null) {
            for (var i = 0; i < snapshot.size(); i++) {
                re.add(i);
            }
            return re;
        }

        var size = currContent.length;
        var snapshotSize = snapshot.size();
        if (snapshotSize > size) {
            for (var i = size; i < snapshotSize; i++) {
                re.add(i);
            }
        }

        for (var i = 0; i < size; i++) {
            var each = i < snapshotSize ? snapshot.get(i) : emptyPair;
            var curr = currContent[i];
            if (curr == null) {
                if (each.getFirstValue() != null) {
                    re.add(i);
                }
                continue;
            }

            if (!curr.equals(each.getFirstValue()) || curr.getAmount() != each.getSecondValue()) {
                re.add(i);
            }
        }

        

        return re;
    }

    public static List<Pair<ItemStack, Integer>> getInvSnapshot(ItemStack[] invContents) {
        var re = new ArrayList<Pair<ItemStack, Integer>>(invContents.length);
        for (var each : invContents) {
            re.add(each == null ? emptyPair : new Pair<>(each, each.getAmount()));
        }

        return re;
    }
}
