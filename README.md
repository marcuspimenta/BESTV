# BesTV
Android TV App powered by TMDb. 
It is a easy way to find the best TV content, the top movies, series... all of that in your TV.

## About
Sample about Android TV, that shows how to use the Leandback library from Google. The main widgets like BrowseSupportFragment, SearchSupportFragment, DetailsSupportFragment, VerticalSupportFragment are used.

## Implementations
<ul>
<li>Android TV Leandback library</li>
<li>The Movie Database (TMDb) REST API</li>
<li>MVP architecture
<li>Dagger 2
<li>Retrofit 2
<li>RxJava 2  
<li>Glide
<li>Lifecycle Architecture Component  
</ul>

## API Keys
BesTV uses [The Movie DB](https://www.themoviedb.org/) API in order to fetch all the data.
To be able to run this application you have to create an API KEY from The Movie DB and place it in your gradle file.
<br>
```
buildConfigField "String", "TMDB_API_KEY", "YOUR API KEY HERE"
```

## Features
<ul>
<li>Show the top movies and tv shows</li>
<li>Show the top movies and tv shows by genre</li>
<li>Details about a work including the casts, the videos, the similar and recommended works</li>
<li>Details about a cast including the credits</li>
<li>Search the movies and the tv shows by title</li>
</ul>

![](gif/app.gif)

## Licence
```
Copyright (c) 2018 Marcus Pimenta

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
