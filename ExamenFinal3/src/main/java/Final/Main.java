package Final;

import java.util.Scanner;

/**
 *
 * @author ASUS
 */
public class Main {

    public static void main(String[] args) {
        neo4j conexion = new neo4j();
        conexion.conectar();
        String peliculaNombre, actorNombre;
        Scanner scanner = new Scanner(System.in);
        Actor actor = null;
        Pelicula pelicula = null;
        int opcion;

        do {
            System.out.println("\nMenu");
            System.out.println("1. Agregar Actor");
            System.out.println("2. Agregar Pelicula");
            System.out.println("3. Crear Conexion");
            System.out.println("4. Eliminar Actor");
            System.out.println("5. Eliminar Pelicula");
            System.out.println("6. Mostar Actores y Peliculas");
            System.out.println("7. Salir");
            System.out.println("Ingrese una opcion: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    System.out.println("Ingrese el nombre del actor: ");
                    actorNombre = scanner.nextLine();
                    actor = new Actor(actorNombre);
                    conexion.agregarActor(actor);
                    break;
                case 2:
                    System.out.println("Ingrese el nombre de la pelicula: ");
                    peliculaNombre = scanner.nextLine();

                    pelicula = new Pelicula(peliculaNombre);
                    conexion.agregarPelicula(pelicula);
                    break;
                case 3:
                    if (actor != null && pelicula != null) {
                        System.out.println("Ingrese el nombre del actor: ");
                        actorNombre = scanner.nextLine();
                        actor.setNombre(actorNombre);

                        System.out.println("Ingrese el nombre de la pelicula: ");
                        peliculaNombre = scanner.nextLine();
                        pelicula.setTitulo(peliculaNombre);

                        boolean actorExiste = conexion.verificarActorExistente(actor);
                        boolean peliculaExiste = conexion.verificarPeliculaExistente(pelicula);

                        if (actorExiste && peliculaExiste) {

                            conexion.crearRelacionActorPelicula(actor, pelicula);
                            System.out.println("Relacion creada entre el actor y la pelicula");

                        } else {

                            System.out.println("El nombre del actor o de la pelicula no existe en la base de datos");
                        }

                    } else {

                        System.out.println("No hay actores ni peliculas, agregelos");
                    }
                    break;
                case 4:
                    System.out.println("Ingrese el nombre del actor a eliminar: ");
                    actorNombre = scanner.nextLine();
                    actor = new Actor(actorNombre);

                    if (!conexion.verificarActorExistente(actor)) {

                        System.out.println("El actor no existe en la base de datos");

                    } else {

                        conexion.eliminarActor(actor);
                        System.out.println("Actor eliminado");
                    }

                    break;

                case 5:
                    System.out.println("Ingrese el nombre de la pelicula a eliminar: ");
                    peliculaNombre = scanner.nextLine();
                    pelicula = new Pelicula(peliculaNombre);

                    if (!conexion.verificarPeliculaExistente(pelicula)) {

                        System.out.println("La pelicula no existe en la base de datos");

                    } else {

                        conexion.eliminarPelicula(pelicula);
                        System.out.println("Pelicula Eliminada");

                    }
                    break;

                case 6:
                    if (conexion.baseDatosVacia()) {
                        System.out.println("La base de datos esta vacia");
                    } else {
                        conexion.mostrarActoresYPeliculas();
                    }
                    break;
                case 7:
                    System.out.println("Menu Cerrado");
                    conexion.desconectar();

                default:
                    System.out.println("Opcion invalida");
                    break;
            }
        } while (opcion != 7);
    }
}
