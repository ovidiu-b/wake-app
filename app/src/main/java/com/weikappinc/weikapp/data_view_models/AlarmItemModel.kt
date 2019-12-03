package com.weikappinc.weikapp.data_view_models

class AlarmItemModel(
    val id: String,
    hour: String,
    description: String,
    alarmDate: String,
    isActivated: Boolean,
    updatedAt: Long) : ModelBase(updatedAt) {

    var hour: String = hour
        set(value) {
            field = value
            updateUpdatedAt()
        }

    var description: String = description
        set(value) {
            field = value
            updateUpdatedAt()
        }

    var alarmDate: String = alarmDate
        set(value) {
            field = value
            updateUpdatedAt()
        }

    var isActivated: Boolean = isActivated
        set(value) {
            field = value
            updateUpdatedAt()
        }

    private fun updateUpdatedAt() {
        updatedAt = System.currentTimeMillis()
    }
}