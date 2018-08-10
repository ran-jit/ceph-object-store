# Ceph Object Store

## What ?
Ceph, a free-software storage platform, implements object storage on a single distributed computer cluster, and provides interfaces for object-, block- and file-level storage. Ceph aims primarily for completely distributed operation without a single point of failure, scalable to the exabyte level, and highly available.

Ceph replicates data and makes it fault-tolerant using commodity hardware and requiring no specific hardware support.

## Object Storage ?
Ceph implements distributed object storage. Ceph’s software libraries provide client applications with direct access to the reliable autonomic distributed object store (RADOS) object-based storage system, and also provide a foundation for some of Ceph’s features, including RADOS Block Device (RBD), RADOS Gateway, and the Ceph File System.

RADOS
* R -> Reliable
* A -> Autonomic
* D -> Distributed
* O -> Object
* S -> Store
     
RADOS that doesn't have a single point of failure as there is no central component, making it a perfect fit for CenterDevice’s architecture. In contrast to other distributed stores, Ceph uses an algorithm-only method to locate and store an object. This means that every client only needs to apply the CRUSH (Controlled, Scalable, Decentralized Placement of Replicated Data) algorithm to compute the corresponding object disk storage daemon (OSD) that is responsible for storing a particular object.

-----------------------
2018 @ Ranjith Manickam
