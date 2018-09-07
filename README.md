# Ovia Health Movie Searcher

1. *At a high level, how does your app work?*\
The user will search a movie (by the android system, or initiated in the app) to search an endpoint specific to movies,tv shows, games, etc. Currently the app only supports searching movies.

2. *What design decisions did you make and why?*\
	1. Android support libraries are a must for any new application needing support for many devices.
	2. Endpoints allows multiple different api. Future api can be added with minimal effort. 
	3. Contraint layouts helps make layouts more accessible.
	4. Following android guidelines for the use of the search view helps maintain a standard ui that the user may recognize. Also helps future proof for other additions like voice search and more UI features.
	5. Using Fragments allows the use of this search page anywhere that might require it in future updates.
	6. Using SDK 27 and not 28. SDK 28 is still a release candidate and cannot be fully trusted to be bug free. Android is usually very relaxed on non released sdks (for example releasing rc-02 for most libraries but not their dependencies) so we want to ensure that building can be done quickly and efficiently. Raising the SDK level will be done in the future when it is fully released.

3. *What design patterns or architecture might be necessary in the future?*\
Since most of the work for adding different endpoints has already been done, new requirements on apis (more information/less information) are the only thing that would change the need for new architecture.\
A different way of adding endpoints (through gradle or even dynamic means) might be a good step forward to allow different apis to be used to achieve the same or similar thing.

4. *How would we extend your app if we had to add functionality?*\
The endpoints are abstracted and more can be added by just adding another class. Since the architecture is so simple, adding different activities and leaving this one as the search one would be the ideal solution to extending features.

5. What documentation, websites, papers, etc. did you consult for this assignment?\
	1. Mainly Android wiki to understand the search view and how it functioned. The wiki was also used to learn about constraint layouts.
	2. Google was used for quick questions for using Glide (third party image loading library).
6. *What third-party libraries or other tools does your application use? How did you choose each library or framework you used?*\
	1. Android annotations. Keeps most Fragment/Activity UI elements easy to access and helps in keeping code clean.
	2. OkHTTP. Makes network calls simple and easy to use.
	3. Glide. Loading many images on a list is possible but would take very long to optimize correctly. Using this library ensures speed and optimization without having to implement it.
	4. Android support libraries.  
7. *How long did you spend on this exercise? If you had unlimited time to spend on this, how would you spend it and how would you prioritize each item?*\
Between 3 and 4 hours was spent on the project. With unlimited time these features would be implemented in the order of priority:
	1. Load plot by using the api. Unfortunately the plot data was not available on the search endpoint.
	2. Async tasks for searching so that the user can search live and the ability to cancel to not waste data/battery power.
	3. Better network error handling.
	4. Better UI handling for searching progress.
	5. Overall polish of the UI layouts.
8. *If you were going to implement a level of automated testing to prepare this for a production environment, how would you go about doing so?*\
Android unit testing would be done in two steps. One is to emulate all api calls and make sure that there are no programming errors while loading data while also emulating network errors. The second step is to do Android Roboscripts to simulate a user using the UI. These test would have to encase as many flows that a user could take including weird ones like rotating the screen, leaving the app, shutting the screen off, etc. They would help find edge case bugs and regression issues while code is being changed across the app and future proof for newer Android devices.