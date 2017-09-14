$(document).ready(function(){

  // Everything Menu Tab Index
  $('#everything-navigation li a').attr('tabindex', '-1');
  
  // Everything Menu Visibility
  $('.js-menu-trigger').on('click touchstart', function(e){
    $('.js-menu').toggleClass('is-visible');
    $('.js-menu-screen').toggleClass('is-visible');
    e.preventDefault();
  });

  // Screen Overlay
  $('.js-menu-screen').on('click touchstart', function(e){
    $('.js-menu').toggleClass('is-visible');
    $('.js-menu-screen').toggleClass('is-visible');
    e.preventDefault();
  });

  // Open Menu
  var openMenu = function() {
    //$('#everything-menu').attr({
    //  'aria-label': 'Navigation Menu Open'
    //});
    $('#everything-menu').attr({
      'aria-expanded': 'true'
    });
    $('#everything-navigation').attr({
      'aria-hidden' : 'false'
    });
    $('#everything-navigation li a').each(function() {
        $(this).removeAttr('tabindex');
    });
  }

  // Close Menu
  var closeMenu = function() {
    //$('#everything-menu').attr({
    //  'aria-label': 'Navigation Menu'
    //});
    $('#everything-menu').attr({
      'aria-expanded': 'false'
    });
    $('#everything-navigation').attr({
      'aria-hidden' : 'true'
    });
    $('#everything-navigation li a').each(function() {
        $(this).attr('tabindex', '-1');
    });
  }

  // Manage menu button state
  $('#everything-menu').on('click', function(e) {
    e.preventDefault();
    $(this).attr('aria-expanded') == 'true' ? closeMenu() : openMenu();
  });

  // At end of navigation block, return focus to navigation menu button
  $('#everything-navigation li:last-child a').on('keydown', function(e) {
    if (e.keyCode === 9) {
      if (!e.shiftKey) {
        e.preventDefault();
        $('#everything-menu').focus();
      }
    }
  });

  // At start of navigation block, refocus close button on SHIFT+TAB
  $('#everything-navigation li:first-child a').on('keydown', function(e) {
    if (e.keyCode === 9) {
      if (e.shiftKey) {
        e.preventDefault();
        $('#everything-menu').focus();
      }
    }
  });

  // If the menu is visible, always TAB into it from the menu button
  $('[aria-expanded]').on('keydown', function(e) {
    if (e.keyCode === 9) {
      if ($(this).attr('aria-expanded') == 'true') {
        if (!e.shiftKey) {
          e.preventDefault();
          $('#everything-navigation li:first-child a').focus();
        } else {
          if (e.shiftKey) {
            e.preventDefault();
            $('#content').focus();
          }
        }
      }
    }
  });

  // Section Menu
  $('#section-menu').click(function () {
    $('html, body').animate({ scrollTop: $(this).offset().top - 16 }, 600);
    $("#section-menu, #section-navigation").toggleClass("expanded");
    $("#section-menu > span > i").toggleClass("fa-caret-square-o-down fa-caret-square-o-up");
  });

  // Synopsize and Add Read More
  if ($('div.synopsis').children().not('#at-a-glance, .glance').length > 1 && !$('div.synopsis>span').attr('editfieldname')) {
    $('div.synopsis').children().not('p:first, #at-a-glance, .glance').addClass('obscure');
    $('div.synopsis').children().not('p:first, #at-a-glance, .glance').removeClass('reveal');
    $('div.synopsis').after("<span id=\"synopsis-more\"><em>click for more</em></span>");
    $('#synopsis-more > em').click(function () {
      $('html, body').animate({ scrollTop: $(".synopsis").offset().top - 12 }, 600);
      $('div.synopsis').children().not('p:first, #at-a-glance, .glance').not(this).toggleClass('obscure');
      $('div.synopsis').children().not('p:first, #at-a-glance, .glance').not(this).toggleClass('reveal');
      $(this).html((($(this).html() == "click for more") ? "click to hide" : "click for more"));
    });
  }
  else if ($('div.synopsis>span').attr('editfieldname')) {
    $('div.synopsis').find('p:gt(0)').addClass('reveal');
    $('div.synopsis').append("<span id=\"synopsis-more\">&nbsp;</span>");
  }

});