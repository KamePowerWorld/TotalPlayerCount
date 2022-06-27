# TotalPlayerCount

Counts the number of players in your Waterfall/Bungeecord Network.

## About

This is a Waterfall/Bungeecord plugin that makes the number of players displayed in the server list to the total number of players on all servers connected to the proxy.

If you are using multiple proxies, the number of players on the server list will normally only show the number of players on that proxy.  
This plugin allows you to display the number of players on all servers.

The mechanism is as simple as pinging all connected servers every 10 seconds and adding up the number of players.

## For Example
```
[A Proxy: 12 players] [B Proxy: 8 players]

[a Server: 7 players] [b Server: 6 players] [c Server: 7 players]
```

### Before
```
A Proxy -> 12 players  
B Proxy -> 8 players  
```

### After
```
A Proxy -> 20 players (a + b + c = 7 + 6 + 7 = 20 players)
B Proxy -> 20 players (a + b + c = 7 + 6 + 7 = 20 players)
```

### How to Use
1. [Click here](https://github.com/KamePowerWorld/TotalPlayerCount/releases) and Download the plugin JAR file
2. Put it to plugins directory in your Waterfall/Bungeecord proxy.
3. That's it. The number of players in the server list has been changed to the total number of players on connected servers.

## Japanese

サーバーリストに表示する人数を、プロキシにつながっているすべてのサーバーのプレイヤー数を合計した数にするWaterfall/Bungeecordプラグインです。

複数のプロキシを使用している場合、通常はそのプロキシの人数しか表示されませんが、  
このプラグインを導入すると、すべてのサーバーの人数を表示できます。

仕組みは10秒おきにすべての接続されているサーバーにpingを飛ばし、プレイヤー数を合計するだけのシンプルな構造です。
