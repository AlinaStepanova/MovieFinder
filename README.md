# MovieFinder

Find movies to be added to your personal watch- and favorites lists, search for movies by the title.<br />
Review popular, top rated and now playing movies.

<i>Note: work in progress.</i>

The TMDb movie database API: https://www.themoviedb.org/documentation/api


<h3> Active branches: </h3>

<ul><b> dev </b> - contains stable implementation of main functionality and tests.</ul>

<ul><b> feature/paging </b> - is derived from a dev branch, contains changes related to the pagination implementation at Favorites, Watch Later and Search fragments (work in progress).</ul>

<h3> Architecture approach </h3>

<img src="https://user-images.githubusercontent.com/23102335/174061985-b1fe3d85-4198-4b9a-b33f-21952ac57383.png" width="50%">

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

---

<p align="center">
  <img src="https://user-images.githubusercontent.com/23102335/177212391-7c7ea900-b9c8-4358-997e-7d0ca1f05630.jpeg" width="25%">
  <img src="https://user-images.githubusercontent.com/23102335/177212431-633ccef3-2608-4cf8-b30f-32e8ee89f423.jpeg" width="25%">
  <img src="https://user-images.githubusercontent.com/23102335/177212459-e025b521-6f00-4781-b73f-3f797bd721bc.jpeg" width="25%">
</p>

<p align="center">
  <img src="https://user-images.githubusercontent.com/23102335/177212691-50074bd2-7f12-43e0-b62b-f2a5117a3c67.jpeg" width="25%">
  <img src="https://user-images.githubusercontent.com/23102335/177212735-bdd77dde-8c2a-465d-bde3-bd0144595343.jpeg" width="25%">
  <img src="https://user-images.githubusercontent.com/23102335/177212769-f62bb262-10d7-4f49-8662-0f0e0bd720b6.jpeg" width="25%">
</p>

Please, obtain your own API key in order to work with the source code.
