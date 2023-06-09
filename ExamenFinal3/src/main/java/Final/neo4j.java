package Final;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import static org.neo4j.driver.Values.parameters;
import org.neo4j.driver.types.Node;

/**
 *
 * @author ASUS
 */
public class neo4j {

    private Driver driver;

    public void conectar() {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "donald225";

        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public void desconectar() {
        if (driver != null) {
            driver.close();
        }
    }

    public void agregarActor(Actor actor) {
        try ( Session session = driver.session()) {
            //realiza una transaccion mediante la expresion lambda tx
            session.writeTransaction(tx -> {
                tx.run("CREATE (a:Actor {nombre: $nombre})", Values.parameters("nombre", actor.getNombre()));
                return null;
            });
        }
    }

    public void agregarPelicula(Pelicula pelicula) {
        try ( Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (p:Pelicula {titulo: $titulo})", Values.parameters("titulo", pelicula.getTitulo()));
                return null;
            });
        }
    }

    public void crearRelacionActorPelicula(Actor actor, Pelicula pelicula) {
        try ( Session session = driver.session()) {//crea una sesión que es una conexion para ejecutar transacciones
            //session.run ejecuta consultas o transacciones
            //realiza una coincidencia entre actor y pelicula creando una relacion entre los dos nodos
            session.run("MATCH (a:Actor {nombre: $nombreActor}), (p:Pelicula {titulo: $tituloPelicula}) "
                    + "CREATE (a)-[:ACTUA_EN]->(p)",
                    parameters("nombreActor", actor.getNombre(), "tituloPelicula", pelicula.getTitulo()));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void eliminarActor(Actor actor) {
        try ( Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (a:Actor {nombre: $nombre}) DETACH DELETE a", Values.parameters("nombre", actor.getNombre()));
                return null;
            });
        }
    }

    public void eliminarPelicula(Pelicula pelicula) {
        try ( Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (p:Pelicula {titulo: $titulo}) DETACH DELETE p", Values.parameters("titulo", pelicula.getTitulo()));
                return null;
            });
        }
    }

    public boolean verificarActorExistente(Actor actor) {
        try ( Session session = driver.session()) {
            //la consulta busca un nodo con la etiqueta actor y el nombre proporcionado
            Result result = session.run("MATCH (a:Actor {nombre: $nombre}) RETURN a",
                    parameters("nombre", actor.getNombre()));
            return result.hasNext();
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean verificarPeliculaExistente(Pelicula pelicula) {
        try ( Session session = driver.session()) {
            Result result = session.run("MATCH (p:Pelicula {titulo: $titulo}) RETURN p",
                    parameters("titulo", pelicula.getTitulo()));
            return result.hasNext();
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean baseDatosVacia() {
        try ( Session session = driver.session()) {
            //consulta todos los nodos con la etiqueta actor
            Result result = session.run("MATCH (a:Actor) RETURN count(a) AS count");
            int actorCount = result.single().get("count").asInt();

            result = session.run("MATCH (p:Pelicula) RETURN count(p) AS count");
            int peliculaCount = result.single().get("count").asInt();

            return actorCount == 0 && peliculaCount == 0;
        } catch (Exception e) {
            System.out.println("Error al verificar si la base de datos está vacia: " + e.getMessage());
            return false;
        }
    }

    public void mostrarActoresYPeliculas() {
        try ( Session session = driver.session()) {
            //busca todos los nodos actor que estén conectados mediante Actua_En con nodos pelicula
            Result result = session.run("MATCH (a:Actor)-[:ACTUA_EN]->(p:Pelicula) RETURN a, p");

            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();
                Node actorNode = record.get("a").asNode();
                Node peliculaNode = record.get("p").asNode();

                String nombreActor = actorNode.get("nombre").asString();
                String tituloPelicula = peliculaNode.get("titulo").asString();

                System.out.println("Actor: " + nombreActor + ", Pelicula: " + tituloPelicula);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
