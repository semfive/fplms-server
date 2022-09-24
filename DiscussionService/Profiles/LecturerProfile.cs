using AutoMapper;
using DiscussionService.Dtos;
using DiscussionService.Models;

namespace DiscussionService.Profiles
{
    public class LecturerProfile : Profile
    {
        public LecturerProfile()
        {
            CreateMap<CreateLecturerDto, Lecturer>();
            CreateMap<Lecturer, GetLecturerDto>();
        }
    }
}