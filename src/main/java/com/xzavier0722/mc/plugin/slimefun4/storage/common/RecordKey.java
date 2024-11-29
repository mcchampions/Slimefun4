package com.xzavier0722.mc.plugin.slimefun4.storage.common;

import io.github.bakedlibs.dough.collections.Pair;
import lombok.Getter;

import java.util.*;

public class RecordKey extends ScopeKey {
    @Getter
    private final Set<FieldKey> fields;
    @Getter
    private final List<Pair<FieldKey, String>> conditions;
    private volatile String strKey = "";
    private volatile boolean changed = true;

    public RecordKey(DataScope scope) {
        this(scope, EnumSet.noneOf(FieldKey.class));
    }

    public RecordKey(DataScope scope, Set<FieldKey> fields) {
        this(scope, fields, new LinkedList<>());
    }

    public RecordKey(DataScope scope, Set<FieldKey> fields, List<Pair<FieldKey, String>> conditions) {
        super(scope);
        this.fields = fields.isEmpty() ? fields : new HashSet<>(fields);
        this.conditions = conditions.isEmpty() ? conditions : new LinkedList<>(conditions);
    }

    public void addField(FieldKey field) {
        fields.add(field);
        changed = true;
    }

    public void addCondition(FieldKey key, String val) {
        conditions.add(new Pair<>(key, val));
        changed = true;
    }

    public void addCondition(FieldKey key, boolean val) {
        addCondition(key, val ? "1" : "0");
    }

    @Override
    protected String getKeyStr() {
        if (changed) {
            StringBuilder re = new StringBuilder();
            re.append(scope).append("/");
            conditions.forEach(c -> re.append(c.getFirstValue())
                    .append("=")
                    .append(c.getSecondValue())
                    .append("/"));
            fields.forEach(f -> re.append(f).append("/"));
            strKey = re.toString();
            changed = false;
        }

        return strKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof RecordKey other)) {
            return false;
        }

        if (this.scope != other.scope) {
            return false;
        }

        if (this.fields.size() != other.fields.size()) {
            return false;
        }

        int conditionSize = this.conditions.size();
        if (conditionSize != other.conditions.size()) {
            return false;
        }

        for (FieldKey field : this.fields) {
            if (!other.fields.contains(field)) {
                return false;
            }
        }

        for (int i = 0; i < conditionSize; i++) {
            if (!this.conditions.get(i).equals(other.conditions.get(i))) {
                return false;
            }
        }

        return true;
    }
}
