package com.avs.moviefinder.utils

const val POSTER_WIDTH = 1280
const val BASE_URL = "https://api.themoviedb.org/3/movie/"
const val POSTER_URL = "https://image.tmdb.org/t/p/w$POSTER_WIDTH/"
const val API_KEY = "3ae41cba5ffccb37830367a69287433d"
//get single movie
//https://api.themoviedb.org/3/movie/419704?api_key=3ae41cba5ffccb37830367a69287433d
private const val mostPopularUrl =
    "${BASE_URL}popular?api_key=$API_KEY&language=en-US"
private const val topRatedURL =
    "${BASE_URL}top_rated?api_key=$API_KEY&language=en-US"
