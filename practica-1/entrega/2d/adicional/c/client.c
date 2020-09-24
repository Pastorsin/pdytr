#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <math.h>
#include <openssl/md5.h>
#include <stdlib.h>

#ifndef BUFFER_SIZE
#define BUFFER_SIZE 10000000
#endif

void error(char *msg)
{
    perror(msg);
    exit(0);
}

double dwalltime()
{
    double sec;
    struct timeval tv;

    gettimeofday(&tv, NULL);
    sec = tv.tv_sec + tv.tv_usec / 1000.0;
    return sec;
}

int main(int argc, char *argv[])
{
    int sockfd, portno, n, i;
    struct sockaddr_in serv_addr;
    struct hostent *server;
    double timetick;

    static unsigned char bufferIn[BUFFER_SIZE];
    static unsigned char bufferOut[BUFFER_SIZE];
    unsigned char checksum[MD5_DIGEST_LENGTH];

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
    bzero(bufferIn, BUFFER_SIZE);

    /* Cargo el bufferIn en todas sus posiciones con el carácter 'a'.
    En la última posición indico el fin del string con '\0'. */
    for(i = 0; i < BUFFER_SIZE - 1; i++)
        bufferIn[i] = 'a';
    bufferIn[BUFFER_SIZE - 1] = '\0';

    // Se calcula el checksum del mensaje
    MD5(bufferIn, BUFFER_SIZE, checksum);

    // Se convierte el tamaño al órden de bytes del sistema
    int sizeConverted = htonl(BUFFER_SIZE);

    timetick = dwalltime();

    // Se envía el tamaño del mensaje
    n = write(sockfd, &sizeConverted, sizeof(sizeConverted));
    if (n < 0) error("ERROR writing to socket");

    // Se envía el checksum
    n = write(sockfd, checksum, MD5_DIGEST_LENGTH);
    if (n < 0) error("ERROR writing to socket");

    // Se envía el mensaje
    n = write(sockfd, bufferIn, BUFFER_SIZE);
    if (n < 0) error("ERROR writing to socket");

    // Espera recibir una respuesta del servidor
    n = read(sockfd, bufferOut, BUFFER_SIZE);
    if (n < 0) error("ERROR reading from socket");

    printf("%f\n", dwalltime() - timetick);

    return 0;
}
