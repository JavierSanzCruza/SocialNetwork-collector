/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.graph.directed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;


import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

/**
 * Clase que lee de fichero un grafo no dirigido
 * @author Javier Sanz-Cruzado Puig y Monica Villanueva Aylagas
 */
public class DirectedGraphReader
{
    
        public DirectedGraph<String, Integer> readDirectedGraph(String route)
        {
            return readDirectedGraph(route, ",");
        }
	/**
	 * Lee un grafo no dirigido desde un fichero
	 * @param route Ruta del fichero que contiene el grafo
	 * @return Un grafo no dirigido si todo va bien, null si no
	 */
	public DirectedGraph<String, Integer> readDirectedGraph(String route, String splitter)
	{
		try
		{
			int i = 1;
			//Lectura del fichero de peliculas
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(route)));
			DirectedGraph<String, Integer> grafo = new DirectedSparseGraph<String, Integer>();
			// Leemos la linea de cabecera
			String line;
			while((line = br.readLine()) != null)
			{
				String[] data = line.split(splitter);
				grafo.addVertex(data[0]);
				grafo.addVertex(data[1]);
				grafo.addEdge(i, data[0], data[1]);
				++i;
			}
			br.close();
			return grafo;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Escribe un fichero con la distribucion del grafo
	 * @param Nombre del fichero de grafo
	 * 		   Nombre del fichero de salida
	 */
	public static void main(String args[])
	{
		if(args.length != 2)
		{
			System.out.println("Argumentos incorrectos");
			System.out.println("ARGUMENTO 1: Nombre del fichero de grafo");
			System.out.println("ARGUMENTO 2: Nombre del fichero de salida");
		}
		
		DirectedGraphReader dgr = new DirectedGraphReader();
		DirectedGraph<String, Integer> g = dgr.readDirectedGraph(args[0]);
		
		HashMap<Integer, Integer> grados = new HashMap<Integer, Integer>();
		
		int maxgrado = 0;
		for(String vertex : g.getVertices())
		{
			int grado = g.inDegree(vertex);
			if(grados.containsKey(grado))
			{
				grados.put(grado, grados.get(grado)+1);
			}
			else
			{
				grados.put(grado, 1);
				if(grado > maxgrado)
					maxgrado = grado;
			}
		}
		
		try
		{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1])));
			for(int i = 0; i <= maxgrado; ++i)
			{
				Integer grado;
				grado = grados.get(i);
				if(grado != null)
				{
					bw.write(i + "\t" + grado + "\n");
				}
			}
			
			bw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
