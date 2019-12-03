package com.weikappinc.weikapp.eventbus.events.single_alarm_activity

class AlarmAudioEvent (val audioPath: String)
class AlarmDayEvent(val days: List<Int>)
class AlarmDescriptionEvent (val description: String)
class AlarmErrorEvent(val message: String)
class AlarmSaveEvent(val finishActivity: Boolean)
class AlarmTimeEvent (val hour: Int, val minutes: Int)
class AlarmTimeRemainingEvent (val timeRemaining: String)
class AlarmVibrantEvent (val isVibrant: Boolean)