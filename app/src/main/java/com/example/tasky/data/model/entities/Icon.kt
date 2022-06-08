package com.example.tasky.data.model.entities

import android.view.View

data class Icon(
    val type: Enum<IconType>,
    val listener: View.OnClickListener?,
    val visibility : Int = View.VISIBLE
)

enum class IconType {
    ADD_ICON,
    CHECK_ICON,
    NEXT_ICON,
    BACK_ICON,
    EDIT_ICON,
    SEARCH_ICON
}