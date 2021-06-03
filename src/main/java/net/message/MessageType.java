package net.message;

/**
 * an enum class for the message types that need to be handled.
 *
 * @author ygarip
 */
public enum MessageType {
  DISCONNECT,
  STARTGAME,
  PLAYERREADY,
  CONNECT,
  CHATMESSAGE,
  SUBMITMOVE,
  SENDPLAYERDATA,
  GIVETILE,
  EXCHANGETILES,
  UPDATEGAMESETTINGS,
  REFUSECONNECTION,
  ADDAI,
  KICKPLAYER,
  ENDABLE,
  REQUESTVALUES,
  REQUESTDISTRIBUTIONS,
  REQUESTDICTIONARY,
  PLACETILE,
  TURN,
  ERROR,
  ENDGAME
}