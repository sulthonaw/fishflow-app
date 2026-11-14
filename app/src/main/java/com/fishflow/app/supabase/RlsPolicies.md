# RLS (Row Level Security) Policies - FishFlow
Developer: Sulthon

Koneksi: Sulthon handle Supabase config dan keamanan data.
RLS memastikan setiap user hanya bisa akses data yang memang haknya.

## Tabel: users
- SELECT: Semua authenticated user bisa baca profil publik
- UPDATE: Hanya bisa update data sendiri (auth.uid() = id)

## Tabel: catches
- SELECT: Hanya penjual pemilik (auth.uid() = seller_id)
- INSERT: Hanya role PENJUAL, seller_id harus = auth.uid()
- DELETE: Hanya pemilik

## Tabel: products
- SELECT: Semua authenticated user bisa baca (untuk katalog)
- INSERT/UPDATE/DELETE: Hanya penjual pemilik (auth.uid() = seller_id)

## Tabel: orders
- SELECT: buyer_id = auth.uid() ATAU seller_id = auth.uid()
- INSERT: Hanya role WISATAWAN, buyer_id = auth.uid()
- UPDATE: seller_id = auth.uid() (untuk konfirmasi/tolak) ATAU buyer_id = auth.uid() (untuk batal)

## Tabel: order_items
- SELECT: Ikut policy orders (join)
- INSERT: Melalui RPC/function (bukan langsung)

## Tabel: destinations & activities
- SELECT: Semua authenticated user
- INSERT/UPDATE/DELETE: Hanya role ADMIN
