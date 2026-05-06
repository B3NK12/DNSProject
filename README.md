This project was designed and developed for a Fundamentals of Networking class. My group designed a new DNS protocol, with a mix of iteration and recursion.

The user can select one of a few URL's which are stored in the authoritative web server. 
The root and TLD web servers can hold many URL's, but the cache server only holds one.
The user can run simulations of each individual protocol and see the path of the request and response.
Otherwise, all three can be run and the times compared. 

URL.java is the main driver class with the main JFrame.
The NetworkDiagramPanel.java is the driver behind the neat network graph. 
The DNSSimulator.java runs the backend simulator.
