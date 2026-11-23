package com.example.streamtv

data class Item(val id: String, val title: String, val poster: String?, val stream: String)
data class Row(val title: String, val items: List<Item>)
