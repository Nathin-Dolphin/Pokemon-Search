# Program Execution

[!] IMPORTANT PLEASE READ [!] NOTES: 

	'...' is the path from your storage device to the location of the repository (for Windows terminal at least).

	[!] The method of compiling and running was done on a Windows computer, if you are using a MAC, the instructions may be confusing. If this is so, anything before the '>' is the terminal on Windows Powershell showing the current directory. After that, it is the actual command, however when doing 'javac' make sure to compile into the 'class' folder from the 'javacode' folder. [!]

## Create Class Files:
At the specified locations in the terminal, enter these commands in order,

	...> cd ...\Pokemon-Search
	...\Pokemon-Search> javac -d '...\Pokemon-Search\class' *.java
	...\Pokemon-Search> cd class

## Execute Program

	...\Pokemon-Search\class> java PokemonSearch
  	...\Pokemon-Search\class> java PokedexWriter


## PokemonSearch
  ## Version 1.0 Features:
	(Implemented) - Search Pokemon by name
	(Implemented) - Search Pokemon by number
	(Implemented) - Search Pokemon by type
	(Implemented) - Search Pokemon by region
	(Implemented) - Implement a list for type
	(Implemented) - Implement a list for region
	(Implemented) - (Temporary) Print the results to the terminal

  ## Version 2.0 Features:
	(Implemented) - Print the output to the frame instead of the terminal
	(Implemented) - Implement method to automatically find and download the needed json files via URLs
	(Implemented) - Print more information about each pokemon
	(Implemented) - Implement option to search by evolution
	(Implemented) - Add pop-up informational box before starting program

  ## Version 3.0 Features:
	(WIP) - When the user clicks on a pokemon, create a pop-up that has more information on that pokemon
	() - Implement option to search by size
	() - Implement option to search by weight
	() - Implement option to search by weaknessess


# PokedexWriter
  ## Version 1.0 Features:
	(Implemented) - Add every pokemon to the json files
	(Implemented) - Be able to easily modify already existing files

  ## Version 2.0 Features:
	() - When the user clicks on a pokemon, create a pop-up that has more information on that pokemon
	() - Add pokemon size
	() - Add pokemon weight
	() - Add pokemon weakness
	() - Implement a save feature
