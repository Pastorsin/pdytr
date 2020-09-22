#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

//funcion para calcular el tiempo
double dwalltime()
{
    double sec;
    struct timeval tv;

    gettimeofday(&tv, NULL);
    sec = tv.tv_sec + tv.tv_usec;
    return sec;
}

void error(char *msg)
{
    perror(msg);
    exit(0);
}

int main(int argc, char *argv[])
{
    int sockfd, portno, n;
    struct sockaddr_in serv_addr;
    struct hostent *server;
    double timetick;

    char buffer[256];

    if (argc < 3)
    {
        fprintf(stderr, "usage %s hostname port\n", argv[0]);
        exit(0);
    }
    // Toma el numero de puerto de los argumentos
    portno = atoi(argv[2]);

    // Crea el file descriptor del socket para la conexion
    sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd < 0)
        error("ERROR opening socket");

    // Toma la direccion del server de los argumentos
    server = gethostbyname(argv[1]);
    if (server == NULL)
    {
        fprintf(stderr, "ERROR, no such host\n");
        exit(0);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;

    // Copia la direccion ip y el puerto del servidor a la estructura del socket
    bcopy((char *)server->h_addr,
          (char *)&serv_addr.sin_addr.s_addr,
          server->h_length);
    serv_addr.sin_port = htons(portno);

    // Descriptor - direccion - tamaño direccion
    if (connect(sockfd, &serv_addr, sizeof(serv_addr)) < 0)
        error("ERROR connecting");

    bzero(buffer, 256);

    // El mensaje por defecto va a ser de un caracter.
    buffer[0] = 'a';
    timetick = dwalltime();

    // Envia un mensaje al socket
    n = write(sockfd, buffer, strlen(buffer));
    if (n < 0) error("ERROR writing to socket");

    bzero(buffer, 256);

    // Espera recibir una respuesta
    n = read(sockfd, buffer, 255);
    if (n < 0) error("ERROR reading from socket");

    // Se imprime el tiempo de comunicación
    printf("%f\n", ( dwalltime() - timetick - 1) / 2);

    return 0;
}
