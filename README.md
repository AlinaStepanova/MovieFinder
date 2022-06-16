# MovieFinder

Find movies to be added to your personal watch- and favorites lists, search for movies by the title.<br />
Review popular, top rated and now playing movies.

<i>Note: work in progress.</i>

The TMDb movie database API: https://www.themoviedb.org/documentation/api


<h3> Active branches: </h3>

<ul><b> dev </b> - contains stable implementation of main functionality and tests.</ul>

<ul><b> feature/paging </b> - is derived from a dev branch, contains changes related to the pagination implementation at Favorites, Watch Later and Search fragments (work in progress).</ul>

<h3> Architecture approach </h3>

<img src="https://user-images.githubusercontent.com/23102335/174061985-b1fe3d85-4198-4b9a-b33f-21952ac57383.png" width="45%">

<h3> Main libraries used: </h3>

<b> Retrofit </b> - as HTTP client<br>
<b> Dagger 2 </b> - as dependency injection library<br>
<b> RxJava 2 </b> - for execution of asynchronous tasks<br>
<b> Room </b> - for work with SQLite database<br>
<b> Navigation Component </b> - for work with fragments and navigation<br>
<b> Work Manager </b> - for repetitive constraint-based tasks on background<br>
<b> Paging </b> - for loading data in pages and showing it in a Recycler View<br>
<b> Lifecycle </b> - for performing actions in response to a change in the lifecycle status of activities and fragments<br>
<b> Databinding </b> - for binding UI components in layouts to data sources<br>
<b> Picasso </b> - for loading and customizing images<br>
<b> JUnit 4, Mockito </b> - tests<br>


<p align="center">
  <img src="https://user-images.githubusercontent.com/23102335/104854463-8410b100-590f-11eb-92a0-1157cb55e8dc.png" width="28%">
  <img src="https://user-images.githubusercontent.com/23102335/104854464-85da7480-590f-11eb-98be-5fd6712455f3.png" width="28%">
  <img src="https://user-images.githubusercontent.com/23102335/104854466-870ba180-590f-11eb-98ab-367415fb3b08.png" width="28%">
</p>

<p align="center">
  <img src="https://drive.google.com/uc?export=view&id=19W0jVi8MHo5ExgqrC7HXroK3Of0XabyU" width="28%">
  <img src="https://drive.google.com/uc?export=view&id=1IdgRrbndIEGEtaBXxtp5a_Wfeq6MvhIE" width="28%">
</p>

Please, obtain your own API key in order to work with the source code.
