package com.sondertara.common.text.dfa;

import com.sondertara.common.struct.Pair;
import com.sondertara.common.collection.Maps;
import com.sondertara.common.struct.Holder;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

class SimpleDFA {
    public static final int INVALID_STATE = Integer.MIN_VALUE;
    @NonNull
    Map<Integer, Map<String, Integer>> stateTransactions = Maps.newHashMap();

    /**
     * 添加状态转换
     *
     * @param fromState 从哪个状态开始计算
     * @param symbol    遇到的符号是什么
     * @param toState   到哪个状态
     */
    public void addTransaction(int fromState, String symbol, int toState) {
        Map<String, Integer> transactions = Maps.get(stateTransactions, fromState, new Supplier<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> get() {
                return Maps.newHashMap();
            }
        });
        transactions.put(symbol, toState);
        Maps.putIfAbsent(stateTransactions, fromState, () -> transactions);
    }

    public int getNextState(int currentState, String symbol) {
        Map<String, Integer> to = Maps.get(stateTransactions, currentState, Maps.<String, Integer>newImmutableMap());
        return Maps.get(to, symbol, INVALID_STATE);
    }

    public int transformState(int initState, String... symbols) {
        final Holder<Integer> currentState = new Holder<Integer>(initState);
        Stream.of(symbols).filter( new Predicate<String>() {
            @Override
            public boolean test(String symbol) {
                return currentState.get() == INVALID_STATE;
            }
        }).forEach(new Consumer<String>() {
            @Override
            public void accept(String symbol) {
                int next = getNextState(currentState.get(), symbol);
                currentState.set(next);
            }
        });
        return currentState.get();
    }

    public Pair<Integer, Integer> getLastValidState(int initState, String... symbols) {
        // key: index
        // value: state
        final Holder<Pair<Integer, Integer>> currentState = new Holder<Pair<Integer, Integer>>(new Pair<Integer, Integer>(-1, initState));


        for (int index = 0; index < symbols.length; index++) {
            if (currentState.get().getKey()<index){
                Pair<Integer, Integer> indexAndState = currentState.get();
                int next = getNextState(indexAndState.getValue(), symbols[index]);
                if (next > INVALID_STATE) {
                    indexAndState.setKey(index);
                    indexAndState.setValue(next);
                }
            }
        }
        return currentState.get();
    }


}
