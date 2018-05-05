# AutoChannelRank
JTS3ServerMod plugin that assign user to group when joining certain channel.

## How to install
1. Open your server config instance, located at
`/config/server1/JTS3ServerMod_server.cfg`

2. At the end of the file put this config:
```
#############################################
#       AutoChannelRank configuration       #
#############################################
# (required) ID of channel that will give rank upon joining. 
autochannelrank_channel_id = 

# (required) ID of rank that should be given.
autochannelrank_rank_id = 

# Time which user have to spend on the server before obtaining rank in seconds. (default: 3 days)
# autochannelrank_time_spent = 259200

# Custom message when server assigns rank to user.
# Default: Thanks for being part of our community.
# autochannelrank_message_rank_assigned = 

# Custom message when user need to wait before getting rank.
# Default: Hey! You need to wait at least 3 days before getting this rank.
# autochannelrank_message_need_to_wait = 

# Custom message when user tries to join channel already having specified rank.
# Default: Hey! You already have this rank. Shoo!
# autochannelrank_message_already_have_rank = 
```
And fill out required values. Optional values are commented by default.

3. In the same file, search for `bot_functions`,
then add `autochannelrank.jar:autochannelrank`.
If you're going to use another plugins as well, then follow the instructions in the config.

4. Finally, download [latest release](//github.com/kubaska/AutoChannelRank/releases/latest) and drop it into `plugins/`

## Get JTS3ServerMod
Lightweight Teamspeak 3 server bot. Download it [here](//www.stefan1200.de/forum/index.php?topic=2.0)

## Issues / Pull requests
... are always welcome.

## License
MIT. See [LICENSE](LICENSE)
