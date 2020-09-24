/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <math.h>
#include <openssl/md5.h>

void error(char *msg)
{
    perror(msg);
    exit(1);
}

int main(int argc, char *argv[])
{
    int sockfd, newsockfd, portno, clilen;
    unsigned int msgSize;
    unsigned char checksumCalculado[MD5_DIGEST_LENGTH];
    unsigned char checksumRecibido[MD5_DIGEST_LENGTH];
    struct sockaddr_in serv_addr, cli_addr;
    long int n, despla;
    if (argc < 2)
    {
        fprintf(stderr, "ERROR, no port provided\n");
        exit(1);
    }
    
    // Crea el file descriptor del socket para la conexion
    sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd < 0)
        error("ERROR opening socket");
    bzero((char *) &serv_addr, sizeof(serv_addr));

    // Asigna el puerto pasado por argumento
    // Asigna la ip en donde escucha (su propia ip)
    portno = atoi(argv[1]);
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno);

    // Vincula el file descriptor con la direccion y el puerto
    if (bind(sockfd, (struct sockaddr *) &serv_addr,
             sizeof(serv_addr)) < 0)
        error("ERROR on binding");

    // Setea la cantidad que pueden esperar mientras se maneja una conexion
    listen(sockfd, 5);

    // Se bloquea a esperar una conexion
    clilen = sizeof(cli_addr);
    newsockfd = accept(sockfd,
                       (struct sockaddr *) &cli_addr,
                       &clilen);

    // Devuelve un nuevo descriptor por el cual se van a realizar las comunicaciones
    if (newsockfd < 0)
        error("ERROR on accept");

    // Leo el tamaño del mensaje
    n = read(newsockfd, &msgSize, 4);
    if (n < 0) error("ERROR reading from socket");

    // Leo el checksum del mensaje
    n = read(newsockfd, checksumRecibido, MD5_DIGEST_LENGTH);
    if (n < 0) error("ERROR reading from socket");

    // Leo el mensaje recibido
    int bufferSize = ntohl(msgSize);
    printf("Tamaño del mensaje: %d\n", bufferSize);

    unsigned char buffer[bufferSize];
    bzero(buffer, bufferSize);

    despla = 0;

    while(despla < bufferSize)
    {
        n = read(newsockfd, buffer + despla, bufferSize - despla);

        if (n < 0) error("ERROR reading from socket");
        
        despla += n;
        printf("Tamaño de desplazamiento: %ld\n", despla);
    }

    // Se calcula el checksum
    MD5(buffer, bufferSize, checksumCalculado);

    printf("El checksum recibido es: ");
    for (int i = 0; i < MD5_DIGEST_LENGTH; i++)
        printf("%02x", checksumRecibido[i]);
    printf("\n");

    printf("El checksum del mensaje es: ");
    for (int i = 0; i < MD5_DIGEST_LENGTH; i++)
        printf("%02x", checksumCalculado[i]);
    printf("\n");

    if (memcmp(checksumRecibido, checksumCalculado, MD5_DIGEST_LENGTH) == 0)
        printf("Los checksum coinciden, el mensaje es válido\n");
    else
        printf("Los checksum no coinciden\n");

    // Responde al cliente
    n = write(newsockfd, "I got your message", 18);
    if (n < 0) error("ERROR writing to socket");

    return 0;
}
