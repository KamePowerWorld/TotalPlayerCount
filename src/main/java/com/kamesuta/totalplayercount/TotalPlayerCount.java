package com.kamesuta.totalplayercount;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * プロキシにつながっている全サーバーの合計プレイヤー数をサーバーリストに表示するプラグイン
 */
public final class TotalPlayerCount extends Plugin implements Listener {
    /**
     * プレイヤーの合計人数
     */
    private AtomicInteger playerCount = new AtomicInteger();
    /**
     * プレイヤーの名前配列
     */
    private AtomicReference<ServerPing.PlayerInfo[]> playerNames = new AtomicReference<>();

    /**
     * プラグイン有効化処理
     */
    @Override
    public void onEnable() {
        // イベントリスナーを登録
        getProxy().getPluginManager().registerListener(this, this);
        // 10秒に一回プレイヤー人数を取得
        getProxy().getScheduler().schedule(this, this::ping, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * プラグイン無効化処理
     */
    @Override
    public void onDisable() {
    }

    /**
     * 合計プレイヤー数を取得する
     */
    private void ping() {
        // プロキシにつながっているサーバーリストを取得
        Collection<ServerInfo> serverInfoList = getProxy().getConfigurationAdapter().getServers().values();
        // サーバーにpingを飛ばしてプレイヤー情報を取得
        CompletableFuture<Stream<ServerPing>> serverPingFutures = serverInfoList.stream()
                .map(serverInfo -> {
                    CompletableFuture<ServerPing> future = new CompletableFuture<>();
                    serverInfo.ping((ok, err) -> {
                        if (err != null)
                            future.completeExceptionally(err); // 取得できなかった
                        future.complete(ok); // 取得できたら結果を返す
                    });
                    return future;
                })
                .map(future -> future.exceptionally(err -> null)) // 取得できなかったらnull (後で無視する)
                .collect(JoinAllCollector.toJoinAllFuture()); // すべてのプレイヤー情報を取得できたら完了
        // pingの結果を集計
        serverPingFutures
                .thenAccept(serverPings -> {
                    // 全サーバーのプレイヤー情報が取得できたら
                    List<ServerPing.Players> playersList = serverPings
                            .filter(Objects::nonNull) // 取得できなかった情報を除外
                            .map(ServerPing::getPlayers) // プレイヤー情報を抽出
                            .collect(Collectors.toList());
                    playerCount.set(
                            playersList.stream()
                                    .map(ServerPing.Players::getOnline) // オンライン人数を取得
                                    .reduce(0, Integer::sum) // 合計
                    );
                    playerNames.set(
                            playersList.stream()
                                    .map(ServerPing.Players::getSample) // プレイヤーリストを取得
                                    .filter(Objects::nonNull) // 取得できなかったサーバーは無視
                                    .flatMap(Arrays::stream) // すべてのサーバーのプレイヤーリストを連結
                                    .toArray(ServerPing.PlayerInfo[]::new) // 配列に変換
                    );
                });
    }

    /**
     * サーバーリストの取得結果を修正する
     */
    @EventHandler
    public void onPing(ProxyPingEvent event) {
        // 変更前のping
        ServerPing ping = event.getResponse();
        // 変更前のプレイヤー情報
        ServerPing.Players current = ping.getPlayers();

        // 変更前の最大プレイヤー数、合計プレイヤー数、全サーバーの連結プレイヤーリストをセットする
        ping.setPlayers(new ServerPing.Players(current.getMax(), playerCount.get(), playerNames.get()));
        // 変更後のpingをセット
        event.setResponse(ping);
    }
}
