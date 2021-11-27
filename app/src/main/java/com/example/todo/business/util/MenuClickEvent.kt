package com.example.todo.business.util

open class MenuClickEvent


sealed class HomeMenuClickEvent:MenuClickEvent(){
    data class AllFilterClickEvent(val isClicked:Boolean):HomeMenuClickEvent()
    data class ActiveFilterClickEvent(val isClicked:Boolean):HomeMenuClickEvent()
    data class ImportantFilterClickEvent(val isClicked:Boolean):HomeMenuClickEvent()
    data class CompletedFilterClickEvent(val isClicked:Boolean):HomeMenuClickEvent()
    data class RemoveAllCompletedClickEvent(val isClicked:Boolean):HomeMenuClickEvent()
}