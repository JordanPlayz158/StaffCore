package cl.bebt.staffcore.menu.menu.Banlist;

import cl.bebt.staffcore.MSGChanel.SendMsg;
import cl.bebt.staffcore.main;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.menu.menu.Reports.ReportMenu;
import cl.bebt.staffcore.sql.SQLGetter;
import cl.bebt.staffcore.utils.BanPlayer;
import cl.bebt.staffcore.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class ChoseBan extends ReportMenu {
    private static PlayerMenuUtility playerMenuUtility;
    
    private final main plugin;
    
    private final int Id;
    
    public ChoseBan( PlayerMenuUtility playerMenuUtility , main plugin , String p2 , int Id ){
        super( playerMenuUtility , plugin , p2 );
        ChoseBan.playerMenuUtility = playerMenuUtility;
        this.plugin = plugin;
        this.Id = Id;
    }
    
    public static PlayerMenuUtility getPlayerMenuUtility( ){
        return playerMenuUtility;
    }
    
    public String getMenuName( ){
        return utils.chat( "&cUn Ban or Close the ban?" );
    }
    
    public int getSlots( ){
        return 45;
    }
    
    public void handleMenu( InventoryClickEvent e ){
        Player p = ( Player ) e.getWhoClicked( );
        if ( e.getCurrentItem( ).getItemMeta( ).getPersistentDataContainer( ).has( new NamespacedKey( this.plugin , "delete_ban" ) , PersistentDataType.STRING ) ) {
            p.closeInventory( );
            BanPlayer.unBan( p , this.Id );
            e.setCancelled( true );
        } else if ( e.getCurrentItem( ).getItemMeta( ).getPersistentDataContainer( ).has( new NamespacedKey( this.plugin , "close_ban" ) , PersistentDataType.STRING ) ) {
            p.closeInventory( );
            CloseBan( p );
            e.setCancelled( true );
        } else if ( e.getCurrentItem( ).getItemMeta( ).getPersistentDataContainer( ).has( new NamespacedKey( this.plugin , "panel" ) , PersistentDataType.STRING ) ) {
            e.setCancelled( true );
        } else if ( e.getCurrentItem( ).getType( ).equals( Material.BARRIER ) ) {
            p.closeInventory( );
            if ( e.getClick( ).isLeftClick( ) ) {
                p.closeInventory( );
                (new BanManager( main.getPlayerMenuUtility( p ) , main.plugin )).open( p );
            }
        }
    }
    
    private void CloseBan( Player p ){
        String reason = null;
        String created = null;
        String exp = null;
        String baner = null;
        String banned = null;
        String status = "closed";
        if ( utils.mysqlEnabled( ) ) {
            reason = SQLGetter.getBanned( this.Id , "Reason" );
            created = SQLGetter.getBanned( this.Id , "Date" );
            exp = SQLGetter.getBanned( this.Id , "ExpDate" );
            baner = SQLGetter.getBanned( this.Id , "Baner" );
            banned = SQLGetter.getBanned( this.Id , "Name" );
            SQLGetter.setBan( this.Id , "closed" );
        } else {
            this.plugin.bans.reloadConfig( );
            reason = this.plugin.bans.getConfig( ).getString( "bans." + this.Id + ".reason" );
            created = this.plugin.bans.getConfig( ).getString( "bans." + this.Id + ".date" );
            exp = this.plugin.bans.getConfig( ).getString( "bans." + this.Id + ".expdate" );
            baner = this.plugin.bans.getConfig( ).getString( "bans." + this.Id + ".banned_by" );
            banned = this.plugin.bans.getConfig( ).getString( "bans." + this.Id + ".name" );
            this.plugin.bans.getConfig( ).set( "bans." + this.Id + ".status" , "closed" );
            this.plugin.bans.getConfig( ).set( "count" , Integer.valueOf( playerMenuUtility.currentBans( ) ) );
            this.plugin.bans.saveConfig( );
        }
        SendMsg.sendBanChangeAlert( this.Id , p.getName( ) , baner , banned , reason , exp , created , status , this.plugin.getConfig( ).getString( "bungeecord.server" ) );
        for ( Player people : Bukkit.getOnlinePlayers( ) ) {
            if ( people.hasPermission( "staffcore.staff" ) ) {
                utils.PlaySound( p , "close_ban" );
                for ( String key : main.plugin.getConfig( ).getStringList( "ban.ban_change" ) ) {
                    key = key.replace( "%changed_by%" , p.getName( ) );
                    key = key.replace( "%baner%" , baner );
                    key = key.replace( "%banned%" , banned );
                    key = key.replace( "%id%" , String.valueOf( this.Id ) );
                    key = key.replace( "%reason%" , reason );
                    key = key.replace( "%create_date%" , created );
                    key = key.replace( "%exp_date%" , exp );
                    key = key.replace( "%ban_status%" , "CLOSED" );
                    utils.tell( people , key );
                }
            }
        }
    }
    
    public void setMenuItems( ){
        ArrayList < String > lore = new ArrayList <>( );
        ItemStack delete = new ItemStack( Material.FLOWER_BANNER_PATTERN , 1 );
        ItemStack closeBan = new ItemStack( Material.NAME_TAG , 1 );
        ItemMeta delete_meta = delete.getItemMeta( );
        ItemMeta closeBan_meta = closeBan.getItemMeta( );
        delete_meta.setDisplayName( utils.chat( "&4UNBAN" ) );
        closeBan_meta.setDisplayName( utils.chat( "&4CLOSE BAN" ) );
        delete_meta.addEnchant( Enchantment.DURABILITY , 1 , true );
        delete_meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
        delete_meta.addItemFlags( ItemFlag.HIDE_ATTRIBUTES );
        closeBan_meta.addEnchant( Enchantment.DURABILITY , 1 , true );
        closeBan_meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
        closeBan_meta.addItemFlags( ItemFlag.HIDE_ATTRIBUTES );
        lore.add( utils.chat( "&8Click to &aUn Ban" ) );
        delete_meta.setLore( lore );
        lore.clear( );
        lore.add( utils.chat( "&8Click to &aClose &8the Ban" ) );
        closeBan_meta.setLore( lore );
        lore.clear( );
        delete_meta.getPersistentDataContainer( ).set( new NamespacedKey( main.plugin , "delete_ban" ) , PersistentDataType.STRING , "delete_ban" );
        closeBan_meta.getPersistentDataContainer( ).set( new NamespacedKey( main.plugin , "close_ban" ) , PersistentDataType.STRING , "close_ban" );
        delete.setItemMeta( delete_meta );
        closeBan.setItemMeta( closeBan_meta );
        int i;
        for ( i = 0; i < 10; i++ ) {
            if ( this.inventory.getItem( i ) == null )
                this.inventory.setItem( i , bluePanel( ) );
        }
        for ( i = 10; i < 17; i++ ) {
            if ( this.inventory.getItem( i ) == null )
                this.inventory.setItem( i , redPanel( ) );
        }
        this.inventory.setItem( 17 , bluePanel( ) );
        this.inventory.setItem( 18 , bluePanel( ) );
        this.inventory.setItem( 19 , redPanel( ) );
        this.inventory.setItem( 25 , redPanel( ) );
        this.inventory.setItem( 26 , bluePanel( ) );
        this.inventory.setItem( 27 , bluePanel( ) );
        for ( i = 28; i < 35; i++ ) {
            if ( this.inventory.getItem( i ) == null )
                this.inventory.setItem( i , redPanel( ) );
        }
        for ( i = 35; i < 45; i++ ) {
            if ( this.inventory.getItem( i ) == null )
                this.inventory.setItem( i , bluePanel( ) );
        }
        if ( utils.mysqlEnabled( ) ) {
            if ( SQLGetter.getBanned( this.Id , "Status" ).equals( "open" ) ) {
                this.inventory.setItem( 20 , delete );
                this.inventory.setItem( 21 , redPanel( ) );
                this.inventory.setItem( 22 , makeItem( Material.BARRIER , ChatColor.DARK_RED + "closed" ) );
                this.inventory.setItem( 23 , redPanel( ) );
                this.inventory.setItem( 24 , closeBan );
            } else if ( SQLGetter.getBanned( this.Id , "Status" ).equals( "closed" ) ) {
                this.inventory.setItem( 20 , redPanel( ) );
                this.inventory.setItem( 21 , delete );
                this.inventory.setItem( 22 , redPanel( ) );
                this.inventory.setItem( 23 , makeItem( Material.BARRIER , ChatColor.DARK_RED + "closed" ) );
                this.inventory.setItem( 24 , redPanel( ) );
            }
        } else if ( this.plugin.bans.getConfig( ).get( "bans." + this.Id + ".status" ).equals( "open" ) ) {
            this.inventory.setItem( 20 , delete );
            this.inventory.setItem( 21 , redPanel( ) );
            this.inventory.setItem( 22 , makeItem( Material.BARRIER , ChatColor.DARK_RED + "closed" ) );
            this.inventory.setItem( 23 , redPanel( ) );
            this.inventory.setItem( 24 , closeBan );
        } else if ( this.plugin.bans.getConfig( ).get( "bans." + this.Id + ".status" ).equals( "closed" ) ) {
            this.inventory.setItem( 20 , redPanel( ) );
            this.inventory.setItem( 21 , delete );
            this.inventory.setItem( 22 , redPanel( ) );
            this.inventory.setItem( 23 , makeItem( Material.BARRIER , ChatColor.DARK_RED + "closed" ) );
            this.inventory.setItem( 24 , redPanel( ) );
        }
    }
}
